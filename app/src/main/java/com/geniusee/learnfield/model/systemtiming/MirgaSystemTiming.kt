package com.geniusee.learnfield.model.systemtiming

import com.geniusee.learnfield.model.SystemTime

class MirgaSystemTiming(
    override var executor: Executor
) : SystemTiming {

    override fun now(): SystemTime {
        return System.currentTimeMillis() / 1000.0
    }
}