package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryInfo
import com.example.data.model.Channel
import com.example.data.model.CountryInfo
import com.example.data.remote.CuratedChannels
import com.example.ui.components.ChannelCard
import com.example.ui.components.FeaturedHeroCard
import com.example.ui.theme.IptvCard
import com.example.ui.theme.IptvCardBorder
import com.example.ui.theme.IptvPrimary
import com.example.ui.theme.IptvSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ExploreScreen(
    channels: List<Channel>,
    currentPlayingChannel: Channel?,
    favorites: Set<String>,
    selectedCountry: CountryInfo,
    selectedCategory: CategoryInfo,
    searchQuery: String,
    selectedResolution: String,
    isLoading: Boolean,
    onChannelClick: (Channel) -> Unit,
    onFavoriteClick: (Channel) -> Unit,
    onCountrySelect: (CountryInfo) -> Unit,
    onCategorySelect: (CategoryInfo) -> Unit,
    onSearchChange: (String) -> Unit,
    onResolutionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search 15,000+ global channels, countries, genres...", color = TextMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = IptvPrimary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear search",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = IptvCard,
                unfocusedContainerColor = IptvCard,
                focusedBorderColor = IptvPrimary,
                unfocusedBorderColor = IptvCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("search_input_field")
        )

        // Quick Country Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CuratedChannels.COUNTRIES.take(12).forEach { country ->
                val isSelected = selectedCountry.code == country.code
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) IptvPrimary else IptvCard
                        )
                        .clickable { onCountrySelect(country) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("chip_country_${country.code}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(country.flagEmoji, fontSize = 14.sp)
                        Text(
                            text = if (country.code == "GLOBAL") "Global" else country.code,
                            color = if (isSelected) Color(0xFF381E72) else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Quick Category Filter Chips & Quality Filter
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CuratedChannels.CATEGORIES.forEach { category ->
                val isSelected = selectedCategory.id == category.id
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            if (isSelected) IptvPrimary else IptvCard
                        )
                        .clickable { onCategorySelect(category) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("chip_category_${category.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(category.icon, fontSize = 13.sp)
                        Text(
                            text = category.name.split("&")[0].trim(),
                            color = if (isSelected) Color(0xFF381E72) else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Resolution Filters (ALL, 4K, FHD, HD)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quality:",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            listOf("ALL", "4K", "FHD", "HD").forEach { res ->
                val isSelected = selectedResolution.equals(res, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) IptvPrimary.copy(alpha = 0.2f) else IptvCard
                        )
                        .clickable { onResolutionChange(res) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = res,
                        color = if (isSelected) IptvPrimary else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        color = IptvPrimary,
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Loading feeds...",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            } else {
                Text(
                    text = "${channels.size} live channels",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Main Channel Feed
        if (channels.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tv,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No channels match your filter",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Try searching for a different country, category, or clear search",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Hero Banner for NASA / Top Live Stream when no search active
                if (searchQuery.isBlank() && selectedCountry.code == "GLOBAL" && selectedCategory.id == "all" && channels.isNotEmpty()) {
                    item {
                        FeaturedHeroCard(
                            channel = channels.first(),
                            onPlayClick = { onChannelClick(channels.first()) },
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                }

                itemsIndexed(channels, key = { index, channel -> "${channel.id}_$index" }) { _, channel ->
                    ChannelCard(
                        channel = channel,
                        isCurrentPlaying = currentPlayingChannel?.id == channel.id,
                        isFavorite = favorites.contains(channel.id),
                        onPlayClick = { onChannelClick(channel) },
                        onFavoriteClick = { onFavoriteClick(channel) }
                    )
                }
            }
        }
    }
}
