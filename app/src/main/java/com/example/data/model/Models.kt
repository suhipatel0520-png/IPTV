package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Channel(
    val id: String,
    val name: String,
    val logo: String? = null,
    val url: String,
    val countryCode: String = "GLOBAL",
    val countryName: String = "Global",
    val category: String = "General",
    val language: String = "English",
    val resolution: String = "HD",
    val isNsfw: Boolean = false,
    val altUrls: List<String> = emptyList(),
    val website: String? = null
)

@Entity(tableName = "favorite_channels")
data class FavoriteChannelEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logo: String?,
    val url: String,
    val countryCode: String,
    val countryName: String,
    val category: String,
    val language: String,
    val resolution: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val logo: String?,
    val url: String,
    val countryCode: String,
    val countryName: String,
    val category: String,
    val language: String,
    val resolution: String,
    val lastWatchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_playlists")
data class CustomPlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val url: String,
    val channelCount: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)

data class CountryInfo(
    val code: String,
    val name: String,
    val flagEmoji: String,
    val channelCount: Int = 0
)

data class CategoryInfo(
    val id: String,
    val name: String,
    val icon: String,
    val channelCount: Int = 0
)

enum class AspectRatioMode(val label: String) {
    FIT("Fit"),
    FILL("Fill"),
    ZOOM("Zoom"),
    RATIO_16_9("16:9"),
    RATIO_4_3("4:3")
}
