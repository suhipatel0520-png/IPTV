package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Channel
import com.example.data.model.FavoriteChannelEntity
import com.example.data.model.WatchHistoryEntity
import com.example.ui.components.ChannelCard
import com.example.ui.theme.IptvGold
import com.example.ui.theme.IptvLiveRed
import com.example.ui.theme.IptvPrimary
import com.example.ui.theme.IptvSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteChannelEntity>,
    watchHistory: List<WatchHistoryEntity>,
    currentPlayingChannel: Channel?,
    onChannelClick: (Channel) -> Unit,
    onRemoveFavorite: (Channel) -> Unit,
    onClearHistory: () -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "My Library",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
        )

        // Tab Selector (Favorites vs Watch History)
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = IptvSurfaceVariant,
            contentColor = IptvPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = IptvPrimary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Favorites (${favorites.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.Star, contentDescription = null, tint = IptvGold) },
                modifier = Modifier.testTag("tab_favorites")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("History (${watchHistory.size})", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Filled.History, contentDescription = null) },
                modifier = Modifier.testTag("tab_history")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            // Favorites List
            if (favorites.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.Star,
                    title = "No Favorite Channels Yet",
                    subtitle = "Tap the star icon on any channel while browsing to save it here for instant access.",
                    ctaText = "Browse Global Channels",
                    onCtaClick = onExploreClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(favorites, key = { it.id }) { fav ->
                        val ch = Channel(
                            id = fav.id,
                            name = fav.name,
                            logo = fav.logo,
                            url = fav.url,
                            countryCode = fav.countryCode,
                            countryName = fav.countryName,
                            category = fav.category,
                            language = fav.language,
                            resolution = fav.resolution
                        )
                        ChannelCard(
                            channel = ch,
                            isCurrentPlaying = currentPlayingChannel?.id == ch.id,
                            isFavorite = true,
                            onPlayClick = { onChannelClick(ch) },
                            onFavoriteClick = { onRemoveFavorite(ch) }
                        )
                    }
                }
            }
        } else {
            // History List
            if (watchHistory.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.History,
                    title = "No Watch History",
                    subtitle = "Channels you watch will appear here so you can quickly jump back in anytime.",
                    ctaText = "Start Watching",
                    onCtaClick = onExploreClick
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recently Watched",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("btn_clear_history")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Clear",
                                tint = IptvLiveRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text("Clear", color = IptvLiveRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 90.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(watchHistory, key = { it.id }) { item ->
                        val ch = Channel(
                            id = item.id,
                            name = item.name,
                            logo = item.logo,
                            url = item.url,
                            countryCode = item.countryCode,
                            countryName = item.countryName,
                            category = item.category,
                            language = item.language,
                            resolution = item.resolution
                        )
                        val isFav = favorites.any { it.id == ch.id }
                        ChannelCard(
                            channel = ch,
                            isCurrentPlaying = currentPlayingChannel?.id == ch.id,
                            isFavorite = isFav,
                            onPlayClick = { onChannelClick(ch) },
                            onFavoriteClick = { onRemoveFavorite(ch) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    ctaText: String,
    onCtaClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onCtaClick,
                colors = ButtonDefaults.buttonColors(containerColor = IptvPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("btn_empty_explore")
            ) {
                Text(
                    text = ctaText,
                    color = Color(0xFF381E72),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
