package com.geniusee.learnfield.app

import com.geniusee.learnfield.model.SystemTime

object AppConfig {

    object AudioConfig {
        const val AUDIO_SETUP_WAITING_TIME: SystemTime = 0.2
    }

    object Timings {
        const val FLASH_MUSICAL_TICK = 4.0
        const val PLAY_ITEM_MUSICAL_TICK = 32.0
        const val FLASH_DURATION = 100L
    }

}