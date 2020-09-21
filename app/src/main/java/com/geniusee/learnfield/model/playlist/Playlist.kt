package com.geniusee.learnfield.model.playlist

import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.model.playlist.item.ScheduledItem
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface PlaylistReader {
    /// Stream that sends out audio items
    val audioStream: Observable<ScheduledItem<AudioPlayItem>>
}

interface PlaylistWriter {
    /// Enqueue a new audio item
    fun enqueue(item: ScheduledItem<AudioPlayItem>)
}

class Playlist : PlaylistWriter, PlaylistReader {

    /// Stream that sends out audio items
    override val audioStream: Observable<ScheduledItem<AudioPlayItem>>
        get() = audioSubject

    private val audioSubject = PublishSubject.create<ScheduledItem<AudioPlayItem>>()

    override fun enqueue(item: ScheduledItem<AudioPlayItem>) {
        audioSubject.onNext(item)
    }
}