package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.CustomPlaylistEntity
import com.example.data.model.FavoriteChannelEntity
import com.example.data.model.WatchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {
    // Favorites
    @Query("SELECT * FROM favorite_channels ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteChannelEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_channels WHERE id = :channelId)")
    fun isFavorite(channelId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteChannelEntity)

    @Query("DELETE FROM favorite_channels WHERE id = :channelId")
    suspend fun deleteFavoriteById(channelId: String)

    // Watch History
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedAt DESC LIMIT 50")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordWatch(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE id = :channelId")
    suspend fun deleteHistoryById(channelId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearHistory()

    // Custom Playlists
    @Query("SELECT * FROM custom_playlists ORDER BY addedAt DESC")
    fun getAllCustomPlaylists(): Flow<List<CustomPlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomPlaylist(playlist: CustomPlaylistEntity): Long

    @Delete
    suspend fun deleteCustomPlaylist(playlist: CustomPlaylistEntity)
}
