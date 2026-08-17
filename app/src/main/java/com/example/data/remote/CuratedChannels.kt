package com.example.data.remote

import com.example.data.model.CategoryInfo
import com.example.data.model.Channel
import com.example.data.model.CountryInfo

object CuratedChannels {

    val COUNTRIES = listOf(
        CountryInfo("GLOBAL", "Global & Worldwide", "🌍", 15420),
        CountryInfo("US", "United States", "🇺🇸", 2150),
        CountryInfo("GB", "United Kingdom", "🇬🇧", 1140),
        CountryInfo("CA", "Canada", "🇨🇦", 480),
        CountryInfo("IN", "India", "🇮🇳", 1680),
        CountryInfo("FR", "France", "🇫🇷", 890),
        CountryInfo("DE", "Germany", "🇩🇪", 950),
        CountryInfo("ES", "Spain", "🇪🇸", 730),
        CountryInfo("IT", "Italy", "🇮🇹", 670),
        CountryInfo("JP", "Japan", "🇯🇵", 380),
        CountryInfo("AU", "Australia", "🇦🇺", 320),
        CountryInfo("BR", "Brazil", "🇧🇷", 810),
        CountryInfo("MX", "Mexico", "🇲🇽", 540),
        CountryInfo("AR", "Argentina", "🇦🇷", 410),
        CountryInfo("TR", "Turkey", "🇹🇷", 620),
        CountryInfo("KR", "South Korea", "🇰🇷", 290),
        CountryInfo("NL", "Netherlands", "🇳🇱", 260),
        CountryInfo("ID", "Indonesia", "🇮🇩", 490),
        CountryInfo("PK", "Pakistan", "🇵🇰", 340),
        CountryInfo("SA", "Saudi Arabia", "🇸🇦", 310),
        CountryInfo("AE", "United Arab Emirates", "🇦🇪", 240),
        CountryInfo("EG", "Egypt", "🇪🇬", 280),
        CountryInfo("ZA", "South Africa", "🇿🇦", 190),
        CountryInfo("NG", "Nigeria", "🇳🇬", 220),
        CountryInfo("UA", "Ukraine", "🇺🇦", 310),
        CountryInfo("PL", "Poland", "🇵🇱", 370),
        CountryInfo("SE", "Sweden", "🇸🇪", 150),
        CountryInfo("NO", "Norway", "🇳🇴", 130),
        CountryInfo("FI", "Finland", "🇫🇮", 110),
        CountryInfo("DK", "Denmark", "🇩🇰", 120),
        CountryInfo("PT", "Portugal", "🇵🇹", 240),
        CountryInfo("GR", "Greece", "🇬🇷", 260)
    )

    val CATEGORIES = listOf(
        CategoryInfo("all", "All Channels", "📺", 15420),
        CategoryInfo("news", "News & 24/7 Live", "📰", 3840),
        CategoryInfo("sports", "Sports & Racing", "⚽", 1920),
        CategoryInfo("movies", "Movies & Cinema", "🍿", 2150),
        CategoryInfo("entertainment", "Entertainment & Series", "🎭", 2890),
        CategoryInfo("music", "Music & Live Concerts", "🎵", 1430),
        CategoryInfo("documentary", "Documentary & Nature", "🌿", 1180),
        CategoryInfo("kids", "Kids & Animation", "🎨", 890),
        CategoryInfo("lifestyle", "Lifestyle & Food", "🍳", 640),
        CategoryInfo("gaming", "Gaming & Tech", "🎮", 480)
    )

    // Pre-loaded high-fidelity streams ready to play with zero waiting
    val FEATURED_CHANNELS = listOf(
        // NASA TV Live HD
        Channel(
            id = "nasa_tv_hd",
            name = "NASA TV Live HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e5/NASA_logo.svg/200px-NASA_logo.svg.png",
            url = "https://ntv1.akamaized.net/hls/live/2014075/NASA-NTV1-HLS/master.m3u8",
            altUrls = listOf(
                "https://d2e1asnsl7br7b.cloudfront.net/7782e205e72f43a79634ecf5fb234595/index.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "Documentary",
            language = "English",
            resolution = "FHD",
            website = "https://nasa.gov"
        ),
        // Euronews English HD
        Channel(
            id = "euronews_en",
            name = "Euronews English HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/Euronews_2016_logo.svg/200px-Euronews_2016_logo.svg.png",
            url = "https://euronews-euronews-world-1-eu.rakuten.wurl.tv/playlist.m3u8",
            altUrls = listOf(
                "https://rakuten-euronews-1-gb.samsung.wurl.tv/playlist.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "GLOBAL",
            countryName = "Global",
            category = "News",
            language = "English",
            resolution = "HD",
            website = "https://euronews.com"
        ),
        // Sky News Live UK
        Channel(
            id = "sky_news_live",
            name = "Sky News UK 24/7 HD",
            logo = "https://upload.wikimedia.org/wikipedia/en/thumb/5/52/Sky_News_logo.svg/200px-Sky_News_logo.svg.png",
            url = "https://rakuten-skynews-1-gb.samsung.wurl.tv/playlist.m3u8",
            altUrls = listOf(
                "https://linear013-gb-dash1-prd-cf.cdn.skycdp.com/016a/unison/dash/live/skynews/skynews.isml/skynews-audio_128000=128000-video=2500000.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "GB",
            countryName = "United Kingdom",
            category = "News",
            language = "English",
            resolution = "FHD"
        ),
        // DW English (Deutsche Welle) HD
        Channel(
            id = "dw_english",
            name = "DW English HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Deutsche_Welle_logo.svg/200px-Deutsche_Welle_logo.svg.png",
            url = "https://dwamdstream102.akamaized.net/hls/live/2015525/dwstream102/index.m3u8",
            altUrls = listOf(
                "https://dwamdstream104.akamaized.net/hls/live/2015530/dwstream104/index.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "DE",
            countryName = "Germany",
            category = "News",
            language = "English",
            resolution = "HD"
        ),
        // France 24 English HD
        Channel(
            id = "france24_en",
            name = "France 24 English HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/87/France_24_logo.svg/200px-France_24_logo.svg.png",
            url = "https://f24hls-i.akamaihd.net/hls/live/221193/F24_EN_LO_HLS/master_500.m3u8",
            altUrls = listOf(
                "https://static.france24.com/live/F24_EN_LO_HLS/master_500.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "FR",
            countryName = "France",
            category = "News",
            language = "English",
            resolution = "HD"
        ),
        // Al Jazeera English HD
        Channel(
            id = "al_jazeera_en",
            name = "Al Jazeera English HD",
            logo = "https://upload.wikimedia.org/wikipedia/en/thumb/f/f2/Al_Jazeera_English_logo.svg/200px-Al_Jazeera_English_logo.svg.png",
            url = "https://live-hls-web-aje.getaj.net/AJE/03.m3u8",
            altUrls = listOf(
                "https://live-hls-web-aje.getaj.net/AJE/index.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "GLOBAL",
            countryName = "Global",
            category = "News",
            language = "English",
            resolution = "HD"
        ),
        // Red Bull TV Sports & Action HD
        Channel(
            id = "redbull_tv",
            name = "Red Bull TV Live Sports HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Red_Bull_TV_logo.svg/200px-Red_Bull_TV_logo.svg.png",
            url = "https://rbmn-live.akamaized.net/hls/live/590964/BoRB-AT/master.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "GLOBAL",
            countryName = "Global",
            category = "Sports",
            language = "English",
            resolution = "FHD"
        ),
        // Bloomberg Financial TV
        Channel(
            id = "bloomberg_tv",
            name = "Bloomberg Financial TV HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/56/Bloomberg_Television_logo.svg/200px-Bloomberg_Television_logo.svg.png",
            url = "https://bloomberg-bloomberg-1-eu.rakuten.wurl.tv/playlist.m3u8",
            altUrls = listOf(
                "https://rakuten-bloomberg-1-gb.samsung.wurl.tv/playlist.m3u8",
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "News",
            language = "English",
            resolution = "FHD"
        ),
        // ABC News Live
        Channel(
            id = "abc_news_live",
            name = "ABC News Live HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2a/ABC_News_logo_2021.svg/200px-ABC_News_logo_2021.svg.png",
            url = "https://content.uplynk.com/channel/3324f2467c414329b3b0cc5da987b6fc.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "News",
            language = "English",
            resolution = "HD"
        ),
        // NHK World Japan HD
        Channel(
            id = "nhk_world_japan",
            name = "NHK World Japan HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/7b/NHK_World-Japan.svg/200px-NHK_World-Japan.svg.png",
            url = "https://nhkwlive-ojp.akamaized.net/hls/live/2003459/nhkwlive-ojp-en/index.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "JP",
            countryName = "Japan",
            category = "News",
            language = "English",
            resolution = "FHD"
        ),
        // RTVE 24 Horas Spain
        Channel(
            id = "rtve_24h_es",
            name = "RTVE 24h España HD",
            logo = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c8/Canal_24_Horas_logo_2008.svg/200px-Canal_24_Horas_logo_2008.svg.png",
            url = "https://rtvelivestream.akamaized.net/rtvesec/24h/24h_main.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "ES",
            countryName = "Spain",
            category = "News",
            language = "Spanish",
            resolution = "HD"
        ),
        // Global Nature & Cinema Live 4K
        Channel(
            id = "nature_cinema_live",
            name = "Global Cinema & Nature HD",
            logo = "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=200&fit=crop",
            url = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8",
            altUrls = listOf(
                "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8",
                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            countryCode = "GLOBAL",
            countryName = "Global",
            category = "Documentary",
            language = "English",
            resolution = "4K"
        ),
        // Classic Cinema HD
        Channel(
            id = "classic_cinema_hd",
            name = "Classic Cinema HD",
            logo = "https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=200&fit=crop",
            url = "https://classicmovies-plex.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "Movies",
            language = "English",
            resolution = "HD"
        ),
        // Retro Toons / Animation
        Channel(
            id = "retro_cartoons_tv",
            name = "Retro Toons 24/7 Kids",
            logo = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=200&fit=crop",
            url = "https://retrocartoons-plex.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "Kids & Animation",
            language = "English",
            resolution = "HD"
        ),
        // Clubbing TV / EDM Music Live
        Channel(
            id = "clubbing_tv_live",
            name = "Clubbing TV Music HD",
            logo = "https://images.unsplash.com/photo-1516450360452-9312f5e86fc7?w=200&fit=crop",
            url = "https://clubbingtv-rakuten.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "FR",
            countryName = "France",
            category = "Music",
            language = "English",
            resolution = "FHD"
        ),
        // Motorvision TV Racing & Supercars
        Channel(
            id = "motorvision_tv",
            name = "Motorvision Sports & Racing",
            logo = "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=200&fit=crop",
            url = "https://motorvision-rakuten.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "DE",
            countryName = "Germany",
            category = "Sports",
            language = "English",
            resolution = "HD"
        ),
        // Gaming & Esports TV HD
        Channel(
            id = "esports_tv_live",
            name = "Esports & Gaming Live HD",
            logo = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=200&fit=crop",
            url = "https://esportstv-plex.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "US",
            countryName = "United States",
            category = "Gaming & Tech",
            language = "English",
            resolution = "FHD"
        ),
        // Bollywood Music Hits HD
        Channel(
            id = "bollywood_hits_hd",
            name = "Bollywood Music Live HD",
            logo = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=200&fit=crop",
            url = "https://bollywood-plex.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "IN",
            countryName = "India",
            category = "Music",
            language = "Hindi",
            resolution = "HD"
        ),
        // K-Pop TV Live Korea
        Channel(
            id = "kpop_live_hd",
            name = "K-Pop Live TV Korea",
            logo = "https://images.unsplash.com/photo-1492684223066-81342ee5ff30?w=200&fit=crop",
            url = "https://kpoptv-plex.amagi.tv/playlist.m3u8",
            altUrls = listOf(
                "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
            ),
            countryCode = "KR",
            countryName = "South Korea",
            category = "Music",
            language = "Korean",
            resolution = "HD"
        )
    )

    // Online open-source IPTV database mirrors
    val ONLINE_M3U_SOURCES = listOf(
        "https://iptv-org.github.io/iptv/index.m3u",
        "https://iptv-org.github.io/iptv/categories/news.m3u",
        "https://iptv-org.github.io/iptv/categories/sports.m3u",
        "https://iptv-org.github.io/iptv/categories/movies.m3u",
        "https://iptv-org.github.io/iptv/categories/music.m3u",
        "https://iptv-org.github.io/iptv/categories/entertainment.m3u",
        "https://iptv-org.github.io/iptv/categories/documentary.m3u",
        "https://iptv-org.github.io/iptv/categories/kids.m3u",
        "https://iptv-org.github.io/iptv/categories/animation.m3u"
    )

    fun getCountryM3uUrl(countryCode: String): String {
        return "https://iptv-org.github.io/iptv/countries/${countryCode.lowercase()}.m3u"
    }

    fun getCategoryM3uUrl(category: String): String {
        val cat = when (category.lowercase()) {
            "news" -> "news"
            "sports" -> "sports"
            "movies" -> "movies"
            "music" -> "music"
            "documentary" -> "documentary"
            "kids", "kids & animation" -> "kids"
            "entertainment" -> "entertainment"
            "lifestyle" -> "lifestyle"
            "gaming & tech", "gaming" -> "gaming"
            else -> "general"
        }
        return "https://iptv-org.github.io/iptv/categories/$cat.m3u"
    }
}
