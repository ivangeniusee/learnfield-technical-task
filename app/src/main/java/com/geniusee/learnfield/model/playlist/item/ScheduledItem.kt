package com.geniusee.learnfield.model.playlist.item

import com.geniusee.learnfield.model.MusicalTick

data class ScheduledItem<T : AudioPlayItem>(
    val item: T,
    val startAt: MusicalTick
)