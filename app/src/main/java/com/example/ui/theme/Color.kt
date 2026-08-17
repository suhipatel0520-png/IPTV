package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Immersive UI Theme Color Palette
val IptvBackground = Color(0xFF1C1B1F)
val IptvSurface = Color(0xFF2B2930)
val IptvSurfaceVariant = Color(0xFF36343B)
val IptvCard = Color(0xFF25232A)
val IptvCardBorder = Color(0xFF49454F)

val IptvPrimary = Color(0xFFD0BCFF)       // Lavender Primary Accent
val IptvPrimaryVariant = Color(0xFF9A82DB)
val IptvSecondary = Color(0xFFCCC2DC)     // Muted Lavender Grey
val IptvTertiary = Color(0xFFEFB8C8)      // Rose Accent
val IptvAccentMint = Color(0xFFB2EEB5)    // Minty Live Accent

val IptvLiveRed = Color(0xFFFF5449)       // High-visibility On-Air Red
val IptvLiveGreen = Color(0xFFB2EEB5)     // Live Stream Green
val IptvGold = Color(0xFFFFD54F)          // Favorite Gold Star

val TextPrimary = Color(0xFFE6E1E5)
val TextSecondary = Color(0xFFCAC4D0)
val TextMuted = Color(0xFF938F99)

val DarkColorScheme = androidx.compose.material3.darkColorScheme(
    primary = IptvPrimary,
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = IptvSecondary,
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    tertiary = IptvTertiary,
    background = IptvBackground,
    onBackground = TextPrimary,
    surface = IptvSurface,
    onSurface = TextPrimary,
    surfaceVariant = IptvSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = IptvCardBorder
)
