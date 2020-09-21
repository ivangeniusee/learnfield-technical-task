package com.geniusee.learnfield.view.audio.player

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.view.audio.AudioPlayer
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.audio.AudioListener
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class AudioMediaPlayer(
    @RawRes val resource: Int,
    private val context: Context
) : AudioPlayer, Player.EventListener {

    //    private var player: MediaPlayer = MediaPlayer.create(context, resource)
    private var player = SimpleExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            false
        )
        .build()
    private var startHandler: ((AudioPlayer) -> Unit)? = null
    private var completionHandler: ((AudioPlayer) -> Unit)? = null

    init {
        buildRawMediaSource()?.let {
            player.setMediaSource(it)
            player.prepare()
            player.playWhenReady = false
        }
//        player.setOnCompletionListener(this)
//        player.setOnPreparedListener {
//            player.seekTo(0)
//        }
    }

    override fun play() {
//        GlobalScope.launch(newSingleThreadContext("test")) {
//            player.start()
//        }
        player.play()
        startHandler?.invoke(this)
    }

    override fun stop() {
        player.stop()
    }

    override fun currentTime(): Long {
        return player.currentPosition
    }

    override fun setStartHandler(startHandler: ((AudioPlayer) -> Unit)?) {
        this.startHandler = startHandler
    }

    override fun setCompletionHandler(completionHandler: ((AudioPlayer) -> Unit)?) {
        this.completionHandler = completionHandler
    }

    override fun duration(): Long {
        return player.duration
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_READY -> {
                player.seekTo(0)
            }
            Player.STATE_ENDED -> {
                completionHandler?.invoke(this)
                completionHandler = null
            }
            else -> return
        }
    }

    private fun buildRawMediaSource(): MediaSource? {
        val rawDataSource = RawResourceDataSource(context)
        rawDataSource.open(DataSpec(RawResourceDataSource.buildRawResourceUri(resource)))
        val mediaItem = MediaItem.fromUri(rawDataSource.uri!!)
        return ProgressiveMediaSource.Factory { rawDataSource }
            .createMediaSource(mediaItem)
    }

}