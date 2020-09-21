package com.geniusee.learnfield.model.musicaltime

import com.geniusee.learnfield.model.MusicalTempo
import com.geniusee.learnfield.model.MusicalTick
import com.geniusee.learnfield.model.SystemTime
import java.util.concurrent.TimeUnit

data class LinearMusicalTime(
    private val referenceSystemTime: SystemTime,
    private val referenceMusicalTime: MusicalTick,
    private val tempo: MusicalTempo
) {

    /// Conversion of system time to musical time
    fun musicalTime(systemTime: SystemTime): MusicalTick {
        return referenceMusicalTime + (tempo * (systemTime - referenceSystemTime) / 60.0)
    }

    /// Conversion of musical time to system time
    fun systemTime(musicalTime: MusicalTick): SystemTime {
        return referenceSystemTime + (musicalTime - referenceMusicalTime) * 60 / tempo
    }
}