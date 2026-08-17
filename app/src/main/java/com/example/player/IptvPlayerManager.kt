package com.example.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.data.model.AspectRatioMode
import com.example.data.model.Channel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentChannel: Channel? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val errorMessage: String? = null,
    val videoWidth: Int = 0,
    val videoHeight: Int = 0,
    val aspectRatioMode: AspectRatioMode = AspectRatioMode.FIT,
    val isSleepTimerActive: Boolean = false,
    val sleepTimerRemainingSeconds: Int = 0,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val activeStreamUrl: String = "",
    val availableAltUrlsCount: Int = 0,
    val currentStreamIndex: Int = 0
)

@OptIn(UnstableApi::class)
class IptvPlayerManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private var exoPlayer: ExoPlayer? = null
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    private var sleepTimerJob: Job? = null
    private var bufferingWatchdogJob: Job? = null
    private var currentStreamUrlIndex = 0

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        if (exoPlayer != null) return
        val appContext = context.applicationContext
        try {
            val trackSelector = DefaultTrackSelector(appContext).apply {
                setParameters(buildUponParameters().setPreferredTextLanguage("en"))
            }

            // HTTP Data source with generous timeouts & standard browser User Agent
            val httpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Mobile Safari/537.36")
                .setConnectTimeoutMs(10000)
                .setReadTimeoutMs(10000)
                .setAllowCrossProtocolRedirects(true)
                .setKeepPostFor302Redirects(true)

            val dataSourceFactory = DefaultDataSource.Factory(appContext, httpDataSourceFactory)

            val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
                .setLiveTargetOffsetMs(3000)
                .setLiveMinOffsetMs(1000)
                .setLiveMaxOffsetMs(15000)

            // Ultra-responsive live load control: starts playing as soon as 500ms is buffered
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    1500,  // minBufferMs
                    20000, // maxBufferMs
                    500,   // bufferForPlaybackMs
                    1000   // bufferForPlaybackAfterRebufferMs
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .setBackBuffer(5000, true)
                .build()

            // Enable decoder fallback for software codecs if hardware acceleration is unavailable
            val renderersFactory = DefaultRenderersFactory(appContext)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER)
                .setEnableDecoderFallback(true)

            exoPlayer = ExoPlayer.Builder(appContext)
                .setTrackSelector(trackSelector)
                .setMediaSourceFactory(mediaSourceFactory)
                .setLoadControl(loadControl)
                .setRenderersFactory(renderersFactory)
                .setWakeMode(C.WAKE_MODE_NETWORK)
                .setHandleAudioBecomingNoisy(true)
                .build().apply {
                    playWhenReady = true
                    addListener(playerListener)
                }
        } catch (e: Exception) {
            try {
                exoPlayer = ExoPlayer.Builder(appContext).build().apply {
                    playWhenReady = true
                    addListener(playerListener)
                }
            } catch (ex: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Player init failure: ${ex.localizedMessage ?: "Unknown"}"
                )
            }
        }
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    private val playerListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    _uiState.value = _uiState.value.copy(isBuffering = true, errorMessage = null)
                }
                Player.STATE_READY -> {
                    bufferingWatchdogJob?.cancel()
                    _uiState.value = _uiState.value.copy(isBuffering = false, errorMessage = null)
                }
                Player.STATE_ENDED -> {
                    bufferingWatchdogJob?.cancel()
                    _uiState.value = _uiState.value.copy(isPlaying = false, isBuffering = false)
                }
                Player.STATE_IDLE -> {
                    _uiState.value = _uiState.value.copy(isPlaying = false)
                }
            }
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            if (isPlaying) {
                bufferingWatchdogJob?.cancel()
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            if (videoSize.width > 0 && videoSize.height > 0) {
                _uiState.value = _uiState.value.copy(
                    videoWidth = videoSize.width,
                    videoHeight = videoSize.height
                )
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            bufferingWatchdogJob?.cancel()
            tryFallbackOrShowError(error)
        }
    }

    private fun tryFallbackOrShowError(error: PlaybackException? = null) {
        val channel = _uiState.value.currentChannel ?: return
        val allUrls = listOf(channel.url) + channel.altUrls
        val nextIndex = currentStreamUrlIndex + 1

        if (nextIndex < allUrls.size) {
            currentStreamUrlIndex = nextIndex
            val fallbackUrl = allUrls[currentStreamUrlIndex]
            _uiState.value = _uiState.value.copy(
                isBuffering = true,
                errorMessage = "Switching to backup stream source (${currentStreamUrlIndex + 1}/${allUrls.size})...",
                activeStreamUrl = fallbackUrl,
                currentStreamIndex = currentStreamUrlIndex
            )
            playDirectUrl(fallbackUrl)
        } else {
            val errorDesc = when (error?.errorCode) {
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Network connection timed out. Tap Retry."
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS -> "Feed is temporarily unavailable. Tap Retry."
                PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED -> "Stream format parsing error. Tap Retry."
                else -> if (error != null) "Stream error (${error.errorCodeName}). Tap Retry." else "Feed is taking too long to respond. Tap Retry."
            }
            _uiState.value = _uiState.value.copy(
                isBuffering = false,
                errorMessage = errorDesc
            )
        }
    }

    fun playChannel(channel: Channel) {
        currentStreamUrlIndex = 0
        val allUrls = listOf(channel.url) + channel.altUrls
        _uiState.value = _uiState.value.copy(
            currentChannel = channel,
            isBuffering = true,
            errorMessage = null,
            activeStreamUrl = channel.url,
            availableAltUrlsCount = allUrls.size,
            currentStreamIndex = 0
        )
        playDirectUrl(channel.url)
    }

    private fun playDirectUrl(url: String) {
        val player = exoPlayer ?: return
        try {
            bufferingWatchdogJob?.cancel()
            val uri = Uri.parse(url.trim())
            val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setLiveConfiguration(
                    MediaItem.LiveConfiguration.Builder()
                        .setMaxPlaybackSpeed(1.2f)
                        .setMinPlaybackSpeed(0.95f)
                        .build()
                )
                .build()

            player.stop()
            player.clearMediaItems()
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()

            // 12-second watchdog: if buffering takes too long, automatically attempt fallback or report
            bufferingWatchdogJob = coroutineScope.launch(Dispatchers.Main) {
                delay(12000)
                if (_uiState.value.isBuffering && !_uiState.value.isPlaying) {
                    tryFallbackOrShowError()
                }
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isBuffering = false,
                errorMessage = "Could not play stream: ${e.localizedMessage ?: "Unknown"}"
            )
        }
    }

    fun switchToNextStreamSource() {
        val channel = _uiState.value.currentChannel ?: return
        val allUrls = listOf(channel.url) + channel.altUrls
        if (allUrls.size <= 1) return
        val nextIndex = (currentStreamUrlIndex + 1) % allUrls.size
        currentStreamUrlIndex = nextIndex
        val nextUrl = allUrls[nextIndex]
        _uiState.value = _uiState.value.copy(
            isBuffering = true,
            errorMessage = null,
            activeStreamUrl = nextUrl,
            currentStreamIndex = nextIndex
        )
        playDirectUrl(nextUrl)
    }

    fun switchStreamSource() = switchToNextStreamSource()

    fun retryCurrentStream() {
        val channel = _uiState.value.currentChannel ?: return
        val allUrls = listOf(channel.url) + channel.altUrls
        val url = if (currentStreamUrlIndex < allUrls.size) allUrls[currentStreamUrlIndex] else channel.url
        _uiState.value = _uiState.value.copy(
            isBuffering = true,
            errorMessage = null
        )
        playDirectUrl(url)
    }

    fun togglePlayPause() {
        val player = exoPlayer ?: return
        if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun cycleAspectRatio() {
        val current = _uiState.value.aspectRatioMode
        val next = when (current) {
            AspectRatioMode.FIT -> AspectRatioMode.FILL
            AspectRatioMode.FILL -> AspectRatioMode.ZOOM
            AspectRatioMode.ZOOM -> AspectRatioMode.RATIO_16_9
            AspectRatioMode.RATIO_16_9 -> AspectRatioMode.RATIO_4_3
            AspectRatioMode.RATIO_4_3 -> AspectRatioMode.FIT
        }
        _uiState.value = _uiState.value.copy(aspectRatioMode = next)
    }

    fun setAspectRatio(mode: AspectRatioMode) {
        _uiState.value = _uiState.value.copy(aspectRatioMode = mode)
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        exoPlayer?.volume = clamped
        _uiState.value = _uiState.value.copy(volume = clamped, isMuted = clamped == 0f)
    }

    fun toggleMute() {
        val currentMuted = _uiState.value.isMuted
        if (currentMuted) {
            val vol = if (_uiState.value.volume > 0f) _uiState.value.volume else 1.0f
            exoPlayer?.volume = vol
            _uiState.value = _uiState.value.copy(isMuted = false, volume = vol)
        } else {
            exoPlayer?.volume = 0f
            _uiState.value = _uiState.value.copy(isMuted = true)
        }
    }

    fun setSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes <= 0) {
            _uiState.value = _uiState.value.copy(
                isSleepTimerActive = false,
                sleepTimerRemainingSeconds = 0
            )
            return
        }

        val totalSeconds = minutes * 60
        _uiState.value = _uiState.value.copy(
            isSleepTimerActive = true,
            sleepTimerRemainingSeconds = totalSeconds
        )

        sleepTimerJob = coroutineScope.launch(Dispatchers.Main) {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _uiState.value = _uiState.value.copy(sleepTimerRemainingSeconds = remaining)
            }
            exoPlayer?.pause()
            _uiState.value = _uiState.value.copy(
                isPlaying = false,
                isSleepTimerActive = false,
                sleepTimerRemainingSeconds = 0
            )
        }
    }

    fun stopAndRelease() {
        bufferingWatchdogJob?.cancel()
        sleepTimerJob?.cancel()
        exoPlayer?.removeListener(playerListener)
        exoPlayer?.release()
        exoPlayer = null
        _uiState.value = PlayerUiState()
    }
}

