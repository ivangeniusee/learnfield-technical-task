package com.geniusee.learnfield.view.audio

import android.util.Log
import com.geniusee.learnfield.app.AppConfig
import com.geniusee.learnfield.model.toMillis
import kotlinx.coroutines.*

enum class AudioSystemOption {
    INPUT,
    OUTPUT,
    MIRGA
}

interface AudioSystemControllerType {
    /// Setup given audio system
    fun setUp(options: List<AudioSystemOption>, completion: () -> Unit)
}

class AudioSystemController : AudioSystemControllerType {

    private companion object {
        val TAG = AudioSystemController::class.java.simpleName
    }

    /// Indication if its currently setting up
    private var isSettingUp = false

    /// Input flag that was used to setup the engine
    private var runningAudioSystems: MutableSet<AudioSystemOption> = mutableSetOf()

    override fun setUp(options: List<AudioSystemOption>, completion: () -> Unit) {
        if (isSettingUp) {
            Log.d(TAG, "setup skipped as one already running")
            return
        }

        isSettingUp = true

        if (options.contains(AudioSystemOption.INPUT) &&
            !runningAudioSystems.contains(AudioSystemOption.INPUT)
        ) {
            runningAudioSystems.add(AudioSystemOption.OUTPUT)
        }

        GlobalScope.launch(Dispatchers.Main) {
            delay(AppConfig.AudioConfig.AUDIO_SETUP_WAITING_TIME.toMillis())
            isSettingUp = false
            completion()
        }
    }

}