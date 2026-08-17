package com.example.data.remote

import com.example.data.model.Channel
import java.io.BufferedReader
import java.io.StringReader

object M3uParser {

    private val EXTINF_REGEX = Regex("""#EXTINF:-?\d*(\s+.*)?""")
    private val TVG_ID_REGEX = Regex("""tvg-id="([^"]*)"""", RegexOption.IGNORE_CASE)
    private val TVG_NAME_REGEX = Regex("""tvg-name="([^"]*)"""", RegexOption.IGNORE_CASE)
    private val TVG_LOGO_REGEX = Regex("""tvg-logo="([^"]*)"""", RegexOption.IGNORE_CASE)
    private val GROUP_TITLE_REGEX = Regex("""group-title="([^"]*)"""", RegexOption.IGNORE_CASE)
    private val TVG_COUNTRY_REGEX = Regex("""tvg-country="([^"]*)"""", RegexOption.IGNORE_CASE)
    private val TVG_LANGUAGE_REGEX = Regex("""tvg-language="([^"]*)"""", RegexOption.IGNORE_CASE)

    fun parse(
        content: String,
        defaultCountry: String = "GLOBAL",
        defaultCategory: String = "General",
        maxChannels: Int = 1200
    ): List<Channel> {
        val channels = ArrayList<Channel>()
        val reader = BufferedReader(StringReader(content))
        var currentTvgId: String? = null
        var currentName: String? = null
        var currentLogo: String? = null
        var currentGroup: String? = null
        var currentCountry: String? = null
        var currentLanguage: String? = null

        var line: String? = reader.readLine()
        var index = 1

        while (line != null && channels.size < maxChannels) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#EXTINF:", ignoreCase = true)) {
                currentTvgId = TVG_ID_REGEX.find(trimmed)?.groupValues?.get(1)
                val rawName = TVG_NAME_REGEX.find(trimmed)?.groupValues?.get(1)
                currentLogo = TVG_LOGO_REGEX.find(trimmed)?.groupValues?.get(1)
                currentGroup = GROUP_TITLE_REGEX.find(trimmed)?.groupValues?.get(1)
                currentCountry = TVG_COUNTRY_REGEX.find(trimmed)?.groupValues?.get(1)
                currentLanguage = TVG_LANGUAGE_REGEX.find(trimmed)?.groupValues?.get(1)

                // The channel name is often after the comma
                val commaIndex = trimmed.lastIndexOf(',')
                currentName = if (commaIndex != -1 && commaIndex < trimmed.length - 1) {
                    trimmed.substring(commaIndex + 1).trim()
                } else {
                    rawName ?: "Channel $index"
                }
            } else if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                // This is a stream URL line
                val streamUrl = trimmed
                if (streamUrl.startsWith("http://", ignoreCase = true) ||
                    streamUrl.startsWith("https://", ignoreCase = true) ||
                    streamUrl.startsWith("rtmp://", ignoreCase = true) ||
                    streamUrl.startsWith("rtsp://", ignoreCase = true)
                ) {
                    val name = currentName?.takeIf { it.isNotBlank() } ?: "Channel $index"
                    val countryCode = (currentCountry?.takeIf { it.isNotBlank() } ?: defaultCountry).uppercase()
                    val category = currentGroup?.takeIf { it.isNotBlank() } ?: defaultCategory
                    val id = currentTvgId?.takeIf { it.isNotBlank() }?.let { "${it}_${index}" } ?: "ch_${index}_${name.hashCode()}"
                    val resolution = when {
                        name.contains("4K", ignoreCase = true) || streamUrl.contains("4k", ignoreCase = true) -> "4K"
                        name.contains("FHD", ignoreCase = true) || name.contains("1080", ignoreCase = true) -> "FHD"
                        name.contains("HD", ignoreCase = true) || name.contains("720", ignoreCase = true) -> "HD"
                        else -> "HD"
                    }

                    channels.add(
                        Channel(
                            id = id,
                            name = name,
                            logo = currentLogo?.takeIf { it.isNotBlank() },
                            url = streamUrl,
                            countryCode = countryCode,
                            countryName = getCountryName(countryCode),
                            category = normalizeCategory(category),
                            language = currentLanguage ?: "Global",
                            resolution = resolution
                        )
                    )
                    index++
                }

                // Reset for next channel
                currentTvgId = null
                currentName = null
                currentLogo = null
                currentGroup = null
                currentCountry = null
                currentLanguage = null
            }
            line = reader.readLine()
        }

        return channels
    }

    fun normalizeCategory(raw: String): String {
        val lower = raw.lowercase()
        return when {
            lower.contains("news") -> "News"
            lower.contains("sport") -> "Sports"
            lower.contains("movie") || lower.contains("cinema") || lower.contains("film") -> "Movies"
            lower.contains("music") || lower.contains("radio") -> "Music"
            lower.contains("doc") || lower.contains("history") || lower.contains("nature") || lower.contains("science") -> "Documentary"
            lower.contains("kid") || lower.contains("child") || lower.contains("cartoon") || lower.contains("anim") -> "Kids & Animation"
            lower.contains("entertain") || lower.contains("series") || lower.contains("drama") -> "Entertainment"
            lower.contains("lifestyle") || lower.contains("cook") || lower.contains("travel") || lower.contains("fashion") -> "Lifestyle"
            lower.contains("gaming") || lower.contains("tech") || lower.contains("esport") -> "Gaming & Tech"
            lower.contains("culture") || lower.contains("relig") -> "Culture"
            else -> "General"
        }
    }

    fun getCountryName(code: String): String {
        return when (code.uppercase()) {
            "US" -> "United States"
            "GB", "UK" -> "United Kingdom"
            "CA" -> "Canada"
            "FR" -> "France"
            "DE" -> "Germany"
            "ES" -> "Spain"
            "IT" -> "Italy"
            "IN" -> "India"
            "JP" -> "Japan"
            "AU" -> "Australia"
            "BR" -> "Brazil"
            "MX" -> "Mexico"
            "NL" -> "Netherlands"
            "TR" -> "Turkey"
            "AR" -> "Argentina"
            "KR" -> "South Korea"
            "RU" -> "Russia"
            "CN" -> "China"
            "ID" -> "Indonesia"
            "PK" -> "Pakistan"
            "NG" -> "Nigeria"
            "EG" -> "Egypt"
            "ZA" -> "South Africa"
            "SA" -> "Saudi Arabia"
            "AE" -> "United Arab Emirates"
            "UA" -> "Ukraine"
            "PL" -> "Poland"
            "SE" -> "Sweden"
            "NO" -> "Norway"
            "FI" -> "Finland"
            "DK" -> "Denmark"
            "PT" -> "Portugal"
            "GR" -> "Greece"
            "GLOBAL" -> "Global / Worldwide"
            else -> code
        }
    }

    fun getFlagEmoji(countryCode: String?): String {
        if (countryCode == null || countryCode.length != 2 || countryCode.equals("GLOBAL", ignoreCase = true)) {
            return "🌍"
        }
        return try {
            val upper = countryCode.uppercase()
            if (upper[0] in 'A'..'Z' && upper[1] in 'A'..'Z') {
                val firstChar = upper[0].code - 'A'.code + 0x1F1E6
                val secondChar = upper[1].code - 'A'.code + 0x1F1E6
                String(Character.toChars(firstChar)) + String(Character.toChars(secondChar))
            } else {
                "🌍"
            }
        } catch (_: Exception) {
            "🌍"
        }
    }
}
