package com.geniusee.learnfield.view.audio

interface AudioPlayer {

    /// Start playing
    fun play()

    /// Stop
    fun stop()

    /// Get current position relative to start
    fun currentTime(): Long

    /// Set a start handler
    fun setStartHandler(startHandler: ((AudioPlayer) -> Unit)?)

    /// Set a completion handler
    fun setCompletionHandler(completionHandler: ((AudioPlayer) -> Unit)?)

    /// Duration of the audio track
    fun duration(): Long
}