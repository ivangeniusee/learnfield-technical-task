package com.geniusee.learnfield.model.musicaltime

import com.geniusee.learnfield.model.MusicalTempo
import com.geniusee.learnfield.model.MusicalTick
import com.geniusee.learnfield.model.SystemTime
import com.geniusee.learnfield.model.systemtiming.ExecutionHandle
import com.geniusee.learnfield.model.systemtiming.SystemTiming

interface MusicalTimeSource {
    /// Reference to system time
    val systemTiming: SystemTiming

    /// Current positioning and tempo
    val positionAndTempo: LinearMusicalTime

    /// Execute closure at the given position - offset in SystemTime
    fun executeAt(position: MusicalTick, runnable: () -> Unit)

    /// Execute closure at the given position - offset in SystemTime
    fun executeAt(position: MusicalTick, offset: SystemTime, runnable: () -> Unit)
}

class LinearMusicalTimeSource(
    private val tempo: MusicalTempo,
    private val systemTime: SystemTiming
) : MusicalTimeSource {

    data class ScheduledRunnable(
        val runnable: () -> Unit,
        val handle: ExecutionHandle,
        val musicalTime: MusicalTick,
        val offset: SystemTime
    )

    override val systemTiming: SystemTiming
        get() = systemTime

    override val positionAndTempo: LinearMusicalTime =
        LinearMusicalTime(systemTime.now(), 0.0, tempo)

    private var scheduledRunnables: MutableMap<ExecutionHandle, ScheduledRunnable> = mutableMapOf()

    override fun executeAt(position: MusicalTick, runnable: () -> Unit) {
        executeAt(position, 0.0, runnable)
    }

    override fun executeAt(position: MusicalTick, offset: SystemTime, runnable: () -> Unit) {
        val handle = createTimerAt(position, offset, runnable)
        scheduledRunnables[handle] = ScheduledRunnable(runnable, handle, position, offset)
    }

    private fun createTimerAt(
        position: MusicalTick,
        offset: SystemTime,
        runnable: () -> Unit
    ): ExecutionHandle {
        return systemTiming.executor.executeIn(dispatchTimeFromPosition(position, offset)) {
            runnable()
        }
    }

    private fun dispatchTimeFromPosition(
        position: MusicalTick,
        offset: SystemTime = 0.0
    ): SystemTime {
        return positionAndTempo.systemTime(position) - systemTiming.now() + offset
    }
}