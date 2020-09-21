package com.geniusee.learnfield.view.audio

import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.model.playlist.item.ScheduledItem

interface AudioFactory {

    /// Create an audio player
    fun create(
        scheduledItem: ScheduledItem<AudioPlayItem>,
        startHandler: ((AudioPlayer) -> Unit)?,
        completionHandler: ((AudioPlayer) -> Unit)?
    ): AudioPlayer?
}