package com.example

import com.example.data.remote.M3uParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testM3uParser() {
        val sampleM3u = """
            #EXTM3U
            #EXTINF:-1 tvg-id="nasa.us" tvg-name="NASA TV" tvg-logo="https://example.com/nasa.png" group-title="Science" tvg-country="US",NASA TV Live HD
            https://example.com/live/nasa.m3u8
            #EXTINF:-1 tvg-id="skynews.uk" tvg-name="Sky News" group-title="News" tvg-country="GB",Sky News UK HD
            https://example.com/live/skynews.m3u8
        """.trimIndent()

        val channels = M3uParser.parse(sampleM3u)
        assertEquals(2, channels.size)
        assertEquals("NASA TV Live HD", channels[0].name)
        assertEquals("US", channels[0].countryCode)
        assertEquals("https://example.com/live/nasa.m3u8", channels[0].url)
        assertEquals("GB", channels[1].countryCode)
        assertEquals("News", channels[1].category)
    }

    @Test
    fun testFlagEmoji() {
        val usFlag = M3uParser.getFlagEmoji("US")
        assertNotNull(usFlag)
        val gbFlag = M3uParser.getFlagEmoji("GB")
        assertNotNull(gbFlag)
    }
}
