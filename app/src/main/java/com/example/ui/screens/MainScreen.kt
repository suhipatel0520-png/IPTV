package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.ChannelInfoDialog
import com.example.ui.components.FullscreenPlayer
import com.example.ui.components.MiniPlayer
import com.example.ui.components.PlaylistImportDialog
import com.example.ui.components.SleepTimerDialog
import com.example.ui.theme.IptvBackground
import com.example.ui.theme.IptvCard
import com.example.ui.theme.IptvCardBorder
import com.example.ui.theme.IptvPrimary
import com.example.ui.theme.IptvSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()
    val filteredChannels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val allChannels by viewModel.allChannels.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val customPlaylists by viewModel.customPlaylists.collectAsStateWithLifecycle()
    val isLoadingFeed by viewModel.isLoadingFeed.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    val favoriteIds = remember(favorites) { favorites.map { it.id }.toSet() }

    LaunchedEffect(uiState.statusMessage) {
        val msg = uiState.statusMessage
        if (msg != null) {
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.clearStatus()
        }
    }

    Box(modifier = modifier.fillMaxSize().background(IptvBackground)) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GLOBAL",
                                color = IptvPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "IPTV",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    },
                    actions = {
                        // Sleep Timer Action
                        IconButton(
                            onClick = { viewModel.showSleepTimerDialog(true) },
                            modifier = Modifier.testTag("top_bar_sleep_timer")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Timer,
                                contentDescription = "Sleep Timer",
                                tint = if (playerState.isSleepTimerActive) IptvPrimary else TextSecondary
                            )
                        }

                        // Add Playlist Action
                        IconButton(
                            onClick = { viewModel.showPlaylistDialog(true) },
                            modifier = Modifier.testTag("top_bar_add_playlist")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Add Playlist",
                                tint = IptvPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = IptvSurface,
                        titleContentColor = TextPrimary
                    )
                )
            },
            bottomBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Mini Player (Only shown if a channel is loaded and not in fullscreen mode)
                    if (playerState.currentChannel != null && !uiState.isFullscreenPlayer) {
                        MiniPlayer(
                            playerState = playerState,
                            onExpandClick = { viewModel.setFullscreenPlayer(true) },
                            onPlayPauseClick = { viewModel.playerManager.togglePlayPause() }
                        )
                    }

                    // Navigation Bar
                    NavigationBar(
                        containerColor = IptvSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MainNavTab.values().forEach { tab ->
                            val isSelected = uiState.currentTab == tab
                            val icon = when (tab) {
                                MainNavTab.EXPLORE -> Icons.Filled.Tv
                                MainNavTab.COUNTRIES -> Icons.Filled.Public
                                MainNavTab.CATEGORIES -> Icons.Filled.Category
                                MainNavTab.FAVORITES -> Icons.Filled.Star
                                MainNavTab.PLAYLISTS -> Icons.Filled.PlaylistPlay
                            }
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = tab.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF381E72),
                                    selectedTextColor = IptvPrimary,
                                    indicatorColor = IptvPrimary,
                                    unselectedIconColor = TextSecondary,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                            )
                        }
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = IptvBackground
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (uiState.currentTab) {
                    MainNavTab.EXPLORE -> {
                        ExploreScreen(
                            channels = filteredChannels,
                            currentPlayingChannel = playerState.currentChannel,
                            favorites = favoriteIds,
                            selectedCountry = uiState.selectedCountry,
                            selectedCategory = uiState.selectedCategory,
                            searchQuery = uiState.searchQuery,
                            selectedResolution = uiState.selectedResolution,
                            isLoading = isLoadingFeed,
                            onChannelClick = { ch ->
                                viewModel.playChannel(ch)
                                viewModel.setFullscreenPlayer(true)
                            },
                            onFavoriteClick = { ch -> viewModel.toggleFavorite(ch) },
                            onCountrySelect = { country -> viewModel.selectCountry(country) },
                            onCategorySelect = { cat -> viewModel.selectCategory(cat) },
                            onSearchChange = { q -> viewModel.setSearchQuery(q) },
                            onResolutionChange = { res -> viewModel.setResolutionFilter(res) }
                        )
                    }

                    MainNavTab.COUNTRIES -> {
                        CountriesScreen(
                            onSelectCountry = { country -> viewModel.selectCountry(country) }
                        )
                    }

                    MainNavTab.CATEGORIES -> {
                        CategoriesScreen(
                            onSelectCategory = { cat -> viewModel.selectCategory(cat) }
                        )
                    }

                    MainNavTab.FAVORITES -> {
                        FavoritesScreen(
                            favorites = favorites,
                            watchHistory = watchHistory,
                            currentPlayingChannel = playerState.currentChannel,
                            onChannelClick = { ch ->
                                viewModel.playChannel(ch)
                                viewModel.setFullscreenPlayer(true)
                            },
                            onRemoveFavorite = { ch -> viewModel.toggleFavorite(ch) },
                            onClearHistory = { viewModel.clearWatchHistory() },
                            onExploreClick = { viewModel.selectTab(MainNavTab.EXPLORE) }
                        )
                    }

                    MainNavTab.PLAYLISTS -> {
                        PlaylistsScreen(
                            customPlaylists = customPlaylists,
                            totalChannelCount = allChannels.size,
                            isSleepTimerActive = playerState.isSleepTimerActive,
                            onAddPlaylistClick = { viewModel.showPlaylistDialog(true) },
                            onDeletePlaylist = { p -> viewModel.deletePlaylist(p) },
                            onSleepTimerClick = { viewModel.showSleepTimerDialog(true) },
                            onLoadSourceClick = { url ->
                                viewModel.importCustomPlaylist("IPTV-Org Feed", url)
                            }
                        )
                    }
                }
            }
        }

        // Fullscreen Player Overlay (animated slide-in)
        AnimatedVisibility(
            visible = uiState.isFullscreenPlayer && playerState.currentChannel != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            val isCurrentFav = playerState.currentChannel?.let { favoriteIds.contains(it.id) } ?: false

            FullscreenPlayer(
                playerState = playerState,
                exoPlayer = viewModel.playerManager.getPlayer(),
                channelList = filteredChannels,
                isFavorite = isCurrentFav,
                onCloseClick = { viewModel.setFullscreenPlayer(false) },
                onPlayPauseClick = { viewModel.playerManager.togglePlayPause() },
                onRetryClick = { viewModel.playerManager.retryCurrentStream() },
                onNextChannelClick = { viewModel.playNextChannel() },
                onPrevChannelClick = { viewModel.playPreviousChannel() },
                onSelectChannel = { ch -> viewModel.playChannel(ch) },
                onToggleFavorite = {
                    playerState.currentChannel?.let { viewModel.toggleFavorite(it) }
                },
                onCycleAspectRatio = { viewModel.playerManager.cycleAspectRatio() },
                onSleepTimerClick = { viewModel.showSleepTimerDialog(true) },
                onInfoClick = { viewModel.showChannelInfoDialog(true) },
                onVolumeChange = { vol -> viewModel.playerManager.setVolume(vol) },
                onToggleMute = { viewModel.playerManager.toggleMute() },
                onSwitchSourceClick = { viewModel.playerManager.switchStreamSource() }
            )
        }

        // Dialogs
        if (uiState.showPlaylistImportDialog) {
            PlaylistImportDialog(
                onDismiss = { viewModel.showPlaylistDialog(false) },
                onImportUrl = { name, url -> viewModel.importCustomPlaylist(name, url) },
                onImportText = { name, text -> viewModel.importCustomM3uText(name, text) }
            )
        }

        if (uiState.showSleepTimerDialog) {
            SleepTimerDialog(
                currentRemainingSeconds = playerState.sleepTimerRemainingSeconds,
                isTimerActive = playerState.isSleepTimerActive,
                onDismiss = { viewModel.showSleepTimerDialog(false) },
                onSelectMinutes = { mins -> viewModel.setSleepTimer(mins) }
            )
        }

        if (uiState.showChannelInfoDialog) {
            ChannelInfoDialog(
                channel = playerState.currentChannel,
                playerState = playerState,
                onDismiss = { viewModel.showChannelInfoDialog(false) }
            )
        }
    }
}
