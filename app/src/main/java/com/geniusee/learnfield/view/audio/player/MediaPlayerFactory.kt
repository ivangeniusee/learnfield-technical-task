package com.geniusee.learnfield.view.audio.player

import android.content.Context
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.model.playlist.item.ScheduledItem
import com.geniusee.learnfield.model.systemtiming.SystemTiming
import com.geniusee.learnfield.view.audio.AudioFactory
import com.geniusee.learnfield.view.audio.AudioPlayer

class MediaPlayerFactory(
    private val context: Context
) : AudioFactory {

    override fun create(
        scheduledItem: ScheduledItem<AudioPlayItem>,
        startHandler: ((AudioPlayer) -> Unit)?,
        completionHandler: ((AudioPlayer) -> Unit)?
    ): AudioPlayer? {
        val player = AudioMediaPlayer(scheduledItem.item.resource, context)
        player.setStartHandler(startHandler)
        player.setCompletionHandler(completionHandler)
        return player
    }

}