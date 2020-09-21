package com.geniusee.learnfield.di

import com.geniusee.learnfield.model.musicaltime.LinearMusicalTimeSource
import com.geniusee.learnfield.model.playlist.Playlist
import com.geniusee.learnfield.model.systemtiming.MirgaSystemTiming
import com.geniusee.learnfield.model.systemtiming.executor.TimerExecutor
import com.geniusee.learnfield.view.audio.AudioSystemController
import com.geniusee.learnfield.view.audio.PlayItemStatus
import com.geniusee.learnfield.view.audio.player.MediaPlayerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object Modules {

    val main = module {
        single {
            TimerExecutor()
        }
        single {
            Playlist()
        }
        single {
            PlayItemStatus()
        }
        single {
            MirgaSystemTiming(get() as TimerExecutor)
        }
        single {
            MediaPlayerFactory(androidContext())
        }
        single {
            AudioSystemController()
        }
        single {
            LinearMusicalTimeSource(
                tempo = 126.0,
                systemTime = get() as MirgaSystemTiming
            )
        }
    }
}