package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AspectRatioMode
import com.example.data.model.CategoryInfo
import com.example.data.model.Channel
import com.example.data.model.CountryInfo
import com.example.data.model.CustomPlaylistEntity
import com.example.data.model.FavoriteChannelEntity
import com.example.data.model.WatchHistoryEntity
import com.example.data.repository.IptvRepository
import com.example.player.IptvPlayerManager
import com.example.player.PlayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavTab(val label: String, val icon: String) {
    EXPLORE("Live TV", "tv"),
    COUNTRIES("Countries", "globe"),
    CATEGORIES("Categories", "grid"),
    FAVORITES("Favorites", "star"),
    PLAYLISTS("Playlists", "playlist")
}

data class MainUiState(
    val currentTab: MainNavTab = MainNavTab.EXPLORE,
    val selectedCountry: CountryInfo = CountryInfo("GLOBAL", "Global & Worldwide", "🌍", 15420),
    val selectedCategory: CategoryInfo = CategoryInfo("all", "All Channels", "📺", 15420),
    val searchQuery: String = "",
    val selectedResolution: String = "ALL",
    val isFullscreenPlayer: Boolean = false,
    val showPlaylistImportDialog: Boolean = false,
    val showSleepTimerDialog: Boolean = false,
    val showChannelInfoDialog: Boolean = false,
    val statusMessage: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val repository = IptvRepository(db.channelDao())
    val playerManager = IptvPlayerManager(application, viewModelScope)

    val playerState: StateFlow<PlayerUiState> = playerManager.uiState

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    val allChannels: StateFlow<List<Channel>> = repository.allChannels
    val isLoadingFeed: StateFlow<Boolean> = repository.isLoadingFeed
    val loadError: StateFlow<String?> = repository.loadError

    val favorites: StateFlow<List<FavoriteChannelEntity>> = repository.favorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchHistory: StateFlow<List<WatchHistoryEntity>> = repository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customPlaylists: StateFlow<List<CustomPlaylistEntity>> = repository.customPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Combined filtered channels
    val filteredChannels: StateFlow<List<Channel>> = combine(
        allChannels,
        _uiState
    ) { channels, state ->
        channels.filter { channel ->
            val matchesCountry = state.selectedCountry.code.equals("GLOBAL", ignoreCase = true) ||
                    channel.countryCode.equals(state.selectedCountry.code, ignoreCase = true)

            val matchesCategory = state.selectedCategory.id.equals("all", ignoreCase = true) ||
                    channel.category.contains(state.selectedCategory.name, ignoreCase = true) ||
                    channel.category.contains(state.selectedCategory.id, ignoreCase = true)

            val matchesSearch = state.searchQuery.isBlank() ||
                    channel.name.contains(state.searchQuery, ignoreCase = true) ||
                    channel.countryName.contains(state.searchQuery, ignoreCase = true) ||
                    channel.category.contains(state.searchQuery, ignoreCase = true) ||
                    channel.language.contains(state.searchQuery, ignoreCase = true)

            val matchesResolution = state.selectedResolution.equals("ALL", ignoreCase = true) ||
                    channel.resolution.equals(state.selectedResolution, ignoreCase = true)

            matchesCountry && matchesCategory && matchesSearch && matchesResolution
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.loadInitialFeed()
        }
    }

    fun selectTab(tab: MainNavTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun selectCountry(country: CountryInfo) {
        _uiState.value = _uiState.value.copy(
            selectedCountry = country,
            currentTab = MainNavTab.EXPLORE
        )
        viewModelScope.launch {
            repository.loadCountryChannels(country.code)
        }
    }

    fun selectCategory(category: CategoryInfo) {
        _uiState.value = _uiState.value.copy(
            selectedCategory = category,
            currentTab = MainNavTab.EXPLORE
        )
        viewModelScope.launch {
            repository.loadCategoryChannels(category.id)
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setResolutionFilter(resolution: String) {
        _uiState.value = _uiState.value.copy(selectedResolution = resolution)
    }

    fun playChannel(channel: Channel) {
        playerManager.playChannel(channel)
        viewModelScope.launch {
            repository.recordWatch(channel)
        }
    }

    fun playNextChannel() {
        val list = filteredChannels.value
        if (list.isEmpty()) return
        val current = playerState.value.currentChannel
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val nextIndex = if (currentIndex in 0 until list.size - 1) currentIndex + 1 else 0
        playChannel(list[nextIndex])
    }

    fun playPreviousChannel() {
        val list = filteredChannels.value
        if (list.isEmpty()) return
        val current = playerState.value.currentChannel
        val currentIndex = list.indexOfFirst { it.id == current?.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else list.size - 1
        playChannel(list[prevIndex])
    }

    fun toggleFavorite(channel: Channel) {
        val isFav = favorites.value.any { it.id == channel.id }
        viewModelScope.launch {
            repository.toggleFavorite(channel, isFav)
            showStatus(if (isFav) "Removed from favorites" else "Added to favorites ⭐")
        }
    }

    fun setFullscreenPlayer(fullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreenPlayer = fullscreen)
    }

    fun showPlaylistDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showPlaylistImportDialog = show)
    }

    fun showSleepTimerDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSleepTimerDialog = show)
    }

    fun showChannelInfoDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showChannelInfoDialog = show)
    }

    fun setSleepTimer(minutes: Int) {
        playerManager.setSleepTimer(minutes)
        showSleepTimerDialog(false)
        if (minutes > 0) {
            showStatus("Sleep timer set for $minutes minutes ⏱️")
        } else {
            showStatus("Sleep timer turned off")
        }
    }

    fun importCustomPlaylist(name: String, url: String) {
        viewModelScope.launch {
            showPlaylistDialog(false)
            showStatus("Importing playlist...")
            val result = repository.loadCustomM3uUrl(name, url)
            result.onSuccess { count ->
                showStatus("Successfully loaded $count channels! 🎉")
            }.onFailure { err ->
                showStatus("Import error: ${err.message}")
            }
        }
    }

    fun importCustomM3uText(name: String, text: String) {
        viewModelScope.launch {
            showPlaylistDialog(false)
            showStatus("Parsing M3U text...")
            val result = repository.importM3uText(name, text)
            result.onSuccess { count ->
                showStatus("Loaded $count channels! 🎉")
            }.onFailure { err ->
                showStatus("Parse error: ${err.message}")
            }
        }
    }

    fun deletePlaylist(playlist: CustomPlaylistEntity) {
        viewModelScope.launch {
            repository.deletePlaylist(playlist)
            showStatus("Playlist removed")
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            showStatus("Watch history cleared")
        }
    }

    fun showStatus(msg: String) {
        _uiState.value = _uiState.value.copy(statusMessage = msg)
    }

    fun clearStatus() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.stopAndRelease()
    }
}
