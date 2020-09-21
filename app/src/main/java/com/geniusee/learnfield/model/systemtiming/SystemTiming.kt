package com.geniusee.learnfield.model.systemtiming

import com.geniusee.learnfield.model.SystemTime

interface SystemTiming {

    /// Time based executor executor
    val executor: Executor

    /// Get the current system time
    fun now(): SystemTime
}

open class ExecutionHandle {

}

interface Executor {

    /// Execute at specific system time
    fun executeIn(position: SystemTime, runnable:(ExecutionHandle) -> Unit): ExecutionHandle
}