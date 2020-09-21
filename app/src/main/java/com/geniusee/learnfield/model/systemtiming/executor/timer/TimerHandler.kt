package com.geniusee.learnfield.model.systemtiming.executor.timer

import com.geniusee.learnfield.model.systemtiming.ExecutionHandle
import java.util.*

data class TimerHandler(val timer: TimerTask) : ExecutionHandle()