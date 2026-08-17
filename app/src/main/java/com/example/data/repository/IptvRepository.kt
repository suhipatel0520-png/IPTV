package com.example.data.repository

import com.example.data.local.ChannelDao
import com.example.data.model.CategoryInfo
import com.example.data.model.Channel
import com.example.data.model.CountryInfo
import com.example.data.model.CustomPlaylistEntity
import com.example.data.model.FavoriteChannelEntity
import com.example.data.model.WatchHistoryEntity
import com.example.data.remote.CuratedChannels
import com.example.data.remote.M3uParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class IptvRepository(
    private val channelDao: ChannelDao
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    // Master channel cache
    private val _allChannels = MutableStateFlow<List<Channel>>(CuratedChannels.FEATURED_CHANNELS)
    val allChannels = _allChannels.asStateFlow()

    private val _isLoadingFeed = MutableStateFlow(false)
    val isLoadingFeed = _isLoadingFeed.asStateFlow()

    private val _loadError = MutableStateFlow<String?>(null)
    val loadError = _loadError.asStateFlow()

    private val loadedSources = mutableSetOf<String>()

    val favorites: Flow<List<FavoriteChannelEntity>> = channelDao.getAllFavorites()
    val watchHistory: Flow<List<WatchHistoryEntity>> = channelDao.getWatchHistory()
    val customPlaylists: Flow<List<CustomPlaylistEntity>> = channelDao.getAllCustomPlaylists()

    suspend fun loadInitialFeed() = withContext(Dispatchers.IO) {
        if (loadedSources.contains("initial")) return@withContext
        _isLoadingFeed.value = true
        _loadError.value = null
        try {
            // Attempt to load live country/category feeds from IPTV-org
            val request = Request.Builder()
                .url("https://iptv-org.github.io/iptv/categories/news.m3u")
                .header("User-Agent", "Mozilla/5.0 GlobalIPTV/1.0")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val parsed = M3uParser.parse(body, defaultCategory = "News")
                    if (parsed.isNotEmpty()) {
                        mergeChannels(parsed)
                    }
                }
            }
            loadedSources.add("initial")
        } catch (e: Exception) {
            // Curated channels ensure app continues working flawlessly
            _loadError.value = "Using high-speed curated live feeds"
        } finally {
            _isLoadingFeed.value = false
        }
    }

    suspend fun loadCountryChannels(countryCode: String): Result<List<Channel>> = withContext(Dispatchers.IO) {
        if (countryCode.equals("GLOBAL", ignoreCase = true)) {
            return@withContext Result.success(_allChannels.value)
        }
        val cacheKey = "country_$countryCode"
        if (loadedSources.contains(cacheKey)) {
            val existing = _allChannels.value.filter { it.countryCode.equals(countryCode, ignoreCase = true) }
            return@withContext Result.success(existing)
        }

        _isLoadingFeed.value = true
        try {
            val url = CuratedChannels.getCountryM3uUrl(countryCode)
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 GlobalIPTV/1.0")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val parsed = M3uParser.parse(body, defaultCountry = countryCode)
                    if (parsed.isNotEmpty()) {
                        mergeChannels(parsed)
                        loadedSources.add(cacheKey)
                        return@withContext Result.success(parsed)
                    }
                }
            }
            Result.failure(Exception("Could not load country channels"))
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoadingFeed.value = false
        }
    }

    suspend fun loadCategoryChannels(categoryId: String): Result<List<Channel>> = withContext(Dispatchers.IO) {
        if (categoryId.equals("all", ignoreCase = true)) {
            return@withContext Result.success(_allChannels.value)
        }
        val cacheKey = "category_$categoryId"
        if (loadedSources.contains(cacheKey)) {
            val existing = _allChannels.value.filter { it.category.contains(categoryId, ignoreCase = true) }
            return@withContext Result.success(existing)
        }

        _isLoadingFeed.value = true
        try {
            val url = CuratedChannels.getCategoryM3uUrl(categoryId)
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 GlobalIPTV/1.0")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val parsed = M3uParser.parse(body, defaultCategory = categoryId)
                    if (parsed.isNotEmpty()) {
                        mergeChannels(parsed)
                        loadedSources.add(cacheKey)
                        return@withContext Result.success(parsed)
                    }
                }
            }
            Result.failure(Exception("Could not load category channels"))
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoadingFeed.value = false
        }
    }

    suspend fun loadCustomM3uUrl(name: String, url: String): Result<Int> = withContext(Dispatchers.IO) {
        _isLoadingFeed.value = true
        try {
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 GlobalIPTV/1.0")
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string()
                if (!body.isNullOrBlank()) {
                    val parsed = M3uParser.parse(body)
                    if (parsed.isNotEmpty()) {
                        mergeChannels(parsed)
                        // Save to custom playlist DB
                        channelDao.insertCustomPlaylist(
                            CustomPlaylistEntity(
                                name = name.ifBlank { "Custom Playlist" },
                                url = url,
                                channelCount = parsed.size
                            )
                        )
                        return@withContext Result.success(parsed.size)
                    }
                }
            }
            Result.failure(Exception("Empty or invalid M3U playlist format"))
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            _isLoadingFeed.value = false
        }
    }

    suspend fun importM3uText(name: String, content: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val parsed = M3uParser.parse(content)
            if (parsed.isNotEmpty()) {
                mergeChannels(parsed)
                channelDao.insertCustomPlaylist(
                    CustomPlaylistEntity(
                        name = name.ifBlank { "Imported Playlist" },
                        url = "custom_text_${System.currentTimeMillis()}",
                        channelCount = parsed.size
                    )
                )
                Result.success(parsed.size)
            } else {
                Result.failure(Exception("No valid channel entries found in text"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Synchronized
    private fun mergeChannels(newChannels: List<Channel>) {
        val current = _allChannels.value.toMutableList()
        val existingIds = current.map { it.id }.toHashSet()
        val existingUrls = current.map { it.url }.toHashSet()

        for (ch in newChannels) {
            if (!existingIds.contains(ch.id) && !existingUrls.contains(ch.url)) {
                current.add(ch)
                existingIds.add(ch.id)
                existingUrls.add(ch.url)
            }
        }
        _allChannels.value = current
    }

    // Room Favorites
    suspend fun toggleFavorite(channel: Channel, isCurrentlyFav: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlyFav) {
            channelDao.deleteFavoriteById(channel.id)
        } else {
            channelDao.insertFavorite(
                FavoriteChannelEntity(
                    id = channel.id,
                    name = channel.name,
                    logo = channel.logo,
                    url = channel.url,
                    countryCode = channel.countryCode,
                    countryName = channel.countryName,
                    category = channel.category,
                    language = channel.language,
                    resolution = channel.resolution
                )
            )
        }
    }

    fun isFavorite(channelId: String): Flow<Boolean> = channelDao.isFavorite(channelId)

    // Room Watch History
    suspend fun recordWatch(channel: Channel) = withContext(Dispatchers.IO) {
        channelDao.recordWatch(
            WatchHistoryEntity(
                id = channel.id,
                name = channel.name,
                logo = channel.logo,
                url = channel.url,
                countryCode = channel.countryCode,
                countryName = channel.countryName,
                category = channel.category,
                language = channel.language,
                resolution = channel.resolution,
                lastWatchedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        channelDao.clearHistory()
    }

    suspend fun deletePlaylist(playlist: CustomPlaylistEntity) = withContext(Dispatchers.IO) {
        channelDao.deleteCustomPlaylist(playlist)
    }

    fun getCountries(): List<CountryInfo> = CuratedChannels.COUNTRIES

    fun getCategories(): List<CategoryInfo> = CuratedChannels.CATEGORIES
}
