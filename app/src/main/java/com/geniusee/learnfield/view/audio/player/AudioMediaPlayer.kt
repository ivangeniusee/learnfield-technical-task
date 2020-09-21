package com.geniusee.learnfield.view.audio.player

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.view.audio.AudioPlayer

class AudioMediaPlayer(
    @RawRes val resource: Int,
    context: Context
) : AudioPlayer, MediaPlayer.OnCompletionListener {

    private var player: MediaPlayer = MediaPlayer.create(context, resource)
    private var startHandler: ((AudioPlayer) -> Unit)? = null
    private var completionHandler: ((AudioPlayer) -> Unit)? = null

    init {
        player.seekTo(0)
        player.setOnCompletionListener(this)
    }

    override fun play() {
        player.start()
        startHandler?.invoke(this)
    }

    override fun stop() {
        player.stop()
    }

    override fun currentTime(): Int {
        return player.currentPosition
    }

    override fun setStartHandler(startHandler: ((AudioPlayer) -> Unit)?) {
        this.startHandler = startHandler
    }

    override fun setCompletionHandler(completionHandler: ((AudioPlayer) -> Unit)?) {
        this.completionHandler = completionHandler
    }

    override fun duration(): Int {
        return player.duration
    }

    override fun onCompletion(mp: MediaPlayer?) {
        completionHandler?.invoke(this)
        completionHandler = null
    }

}