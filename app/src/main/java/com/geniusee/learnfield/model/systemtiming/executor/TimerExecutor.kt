package com.geniusee.learnfield.model.systemtiming.executor

import com.geniusee.learnfield.model.SystemTime
import com.geniusee.learnfield.model.systemtiming.ExecutionHandle
import com.geniusee.learnfield.model.systemtiming.Executor
import com.geniusee.learnfield.model.systemtiming.executor.timer.TimerHandler
import com.geniusee.learnfield.model.toMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

class TimerExecutor() : Executor {

    /// All scheduled timers
    private var scheduledTimers: MutableSet<TimerTask> = mutableSetOf()

    override fun executeIn(
        position: SystemTime,
        runnable: (ExecutionHandle) -> Unit
    ): ExecutionHandle {
        val timer = Timer(false).schedule(position.toMillis()) {
            cancel()
            scheduledTimers.remove(this)
            GlobalScope.launch(Dispatchers.Main) {
                runnable(TimerHandler(this@schedule))
            }
        }
        scheduledTimers.add(timer)
        return TimerHandler(timer)
    }
}