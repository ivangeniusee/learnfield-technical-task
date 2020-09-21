package com.geniusee.learnfield.view.audio

import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

interface PlayItemStatusReader {
    // called when playback started
    val onStarted: Observable<AudioPlayItem>

    // called when playback ended
    val onCompleted: Observable<AudioPlayItem>
}

interface PlayItemStatusWriter {
    fun notifyStart(player: AudioPlayItem)
    fun notifyCompleted(player: AudioPlayItem)
}

class PlayItemStatus : PlayItemStatusReader, PlayItemStatusWriter {

    override val onStarted: Observable<AudioPlayItem>
        get() = onStartedSubject

    override val onCompleted: Observable<AudioPlayItem>
        get() = onCompletedSubject

    private val onStartedSubject = PublishSubject.create<AudioPlayItem>()
    private val onCompletedSubject = PublishSubject.create<AudioPlayItem>()

    override fun notifyStart(player: AudioPlayItem) {
        onStartedSubject.onNext(player)
    }

    override fun notifyCompleted(player: AudioPlayItem) {
        onCompletedSubject.onNext(player)
    }


}