package com.geniusee.learnfield.view.audio

import android.util.Log
import com.geniusee.learnfield.model.musicaltime.MusicalTimeSource
import com.geniusee.learnfield.model.playlist.PlaylistReader
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.model.playlist.item.ScheduledItem
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class AudioItemController(
    playlist: PlaylistReader,
    disposeBag: CompositeDisposable,
    private val musicalTimeSource: MusicalTimeSource,
    private val audioFactory: AudioFactory,
    private val itemStatusWriter: PlayItemStatusWriter
) {

    private companion object {
        val TAG = AudioItemController::class.java.simpleName
    }

    private var preloadedPlayerPool = mutableMapOf<Int, AudioPlayer>()
    private var scheduledPlayers = mutableMapOf<ScheduledItem<AudioPlayItem>, AudioPlayer>()

    init {
        playlist.audioStream.subscribe {
            onAudioIncoming(it)
        }.addTo(disposeBag)
    }

    private fun onAudioIncoming(scheduledItem: ScheduledItem<AudioPlayItem>) {
        val player = getPlayer(scheduledItem,
            startHandler = {
                itemStatusWriter.notifyStart(scheduledItem.item)
            }, completionHandler = {
                itemStatusWriter.notifyCompleted(scheduledItem.item)
                completeScheduledPlayer(scheduledItem)
            })

        if (player == null) {
            Log.d(TAG, "Player for $scheduledItem could not be created")
            return
        }

        scheduledPlayers[scheduledItem] = player

        musicalTimeSource.executeAt(scheduledItem.startAt) {
            player.play()
        }
    }

    private fun getPlayer(
        scheduledItem: ScheduledItem<AudioPlayItem>,
        startHandler: (AudioPlayer) -> Unit,
        completionHandler: (AudioPlayer) -> Unit
    ): AudioPlayer? {
        return preloadedPlayerPool[scheduledItem.item.resource]?.let {
            it.setStartHandler(startHandler)
            it.setCompletionHandler(completionHandler)
            return@let it
        } ?: run {
            return audioFactory.create(scheduledItem, startHandler, completionHandler)
        }
    }

    private fun completeScheduledPlayer(scheduledItem: ScheduledItem<AudioPlayItem>) {
        scheduledPlayers.remove(scheduledItem)
    }
}
