package com.geniusee.learnfield.model.systemtiming

import com.geniusee.learnfield.model.SystemTime
import java.util.*

class MirgaSystemTiming(
    override var executor: Executor
) : SystemTiming {

    override fun now(): SystemTime {
        return Date().time / 1000.0
    }
}