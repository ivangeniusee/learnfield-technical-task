package com.geniusee.learnfield.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.geniusee.learnfield.R
import com.geniusee.learnfield.app.AppConfig
import com.geniusee.learnfield.databinding.ActivityMainBinding
import com.geniusee.learnfield.model.musicaltime.LinearMusicalTimeSource
import com.geniusee.learnfield.model.playlist.Playlist
import com.geniusee.learnfield.model.playlist.item.AudioPlayItem
import com.geniusee.learnfield.model.playlist.item.ScheduledItem
import com.geniusee.learnfield.model.toMillis
import com.geniusee.learnfield.view.audio.AudioItemController
import com.geniusee.learnfield.view.audio.AudioSystemController
import com.geniusee.learnfield.view.audio.AudioSystemOption
import com.geniusee.learnfield.view.audio.PlayItemStatus
import com.geniusee.learnfield.view.audio.player.MediaPlayerFactory
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.android.inject
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.concurrent.schedule

class SyncedAudioPlayerActivity : AppCompatActivity() {

    private companion object {
        val TAG = SyncedAudioPlayerActivity::class.java.simpleName
    }

    private val audioSystemController: AudioSystemController by inject()
    private val musicalTimeSource: LinearMusicalTimeSource by inject()
    private val playlist: Playlist by inject()
    private val audioFactory: MediaPlayerFactory by inject()
    private val playItemStatus: PlayItemStatus by inject()

    private val disposeBag = CompositeDisposable()
    private val playItemsAdapter = PlayItemsAdapter()

    private lateinit var audioItemController: AudioItemController
    private lateinit var binding: ActivityMainBinding

    private var flashTimer: Timer? = null
    private var queue = mutableListOf<AudioPlayItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        monitorStatus()
        start()
    }

    override fun onDestroy() {
        disposeBag.dispose()
        super.onDestroy()
    }

    private fun configure() {
        binding.recyclerView.adapter = playItemsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun start() {
        audioItemController = AudioItemController(
            playlist,
            disposeBag,
            musicalTimeSource,
            audioFactory,
            playItemStatus
        )
        audioSystemController.setUp(listOf(AudioSystemOption.INPUT)) {
            val audio1 = AudioPlayItem(R.raw.android_test_stem_base, "androidTestStemBase")
            val audio2 = AudioPlayItem(R.raw.android_test_stem_hats, "androidTestStemHats")
            val audio3 = AudioPlayItem(R.raw.android_test_stem_perc, "androidTestStemPerc")

            val timeToSchedule = musicalTimeSource.positionAndTempo.musicalTime(
                musicalTimeSource.systemTiming.now() + 5
            )
            playlist.enqueue(
                ScheduledItem(audio1, timeToSchedule + 16)
            )
            playlist.enqueue(
                ScheduledItem(audio2, timeToSchedule + 32)
            )
            playlist.enqueue(
                ScheduledItem(audio3, timeToSchedule)
            )
            musicalTimeSource.executeAt(timeToSchedule) {
                flashPeriodically()
            }
        }
    }

    private fun monitorStatus() {
        playItemStatus.onStarted.subscribe {
            queue.add(it)
            playItemsAdapter.items = queue
            displayAudioPlayItem(it)
        }.addTo(disposeBag)
    }

    private fun flashPeriodically() {
        val now = musicalTimeSource.positionAndTempo.musicalTime(musicalTimeSource.systemTiming.now())
        val period = musicalTimeSource.positionAndTempo.systemTime(now + AppConfig.Timings.FLASH_MUSICAL_TICK) - musicalTimeSource.systemTiming.now()
        flashTimer = fixedRateTimer(startAt = Date(), period = period.toMillis()) {
            flashCircle()
        }
    }

    private fun flashCircle() {
        binding.circleView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        Handler(Looper.getMainLooper()).postDelayed({
            binding.circleView.backgroundTintList =
                ContextCompat.getColorStateList(this@SyncedAudioPlayerActivity, R.color.white)
        }, AppConfig.Timings.FLASH_DURATION)
    }

    private fun displayAudioPlayItem(item: AudioPlayItem) {
        val now = musicalTimeSource.positionAndTempo.musicalTime(musicalTimeSource.systemTiming.now())
        val period = musicalTimeSource.positionAndTempo.systemTime(now + AppConfig.Timings.PLAY_ITEM_MUSICAL_TICK) - musicalTimeSource.systemTiming.now()
        Handler(Looper.getMainLooper()).postDelayed({
            queue.remove(item)
            playItemsAdapter.items = queue
        }, period.toMillis())
    }
}