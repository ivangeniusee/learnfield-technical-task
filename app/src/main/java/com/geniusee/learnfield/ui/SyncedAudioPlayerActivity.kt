package com.geniusee.learnfield.ui

import android.os.Bundle
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
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.android.ext.android.inject
import java.util.*
import java.util.concurrent.TimeUnit

class SyncedAudioPlayerActivity : AppCompatActivity() {

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
            // Display recently started audio play item
            queue.add(it)
            playItemsAdapter.items = queue
            displayAudioPlayItem(it)
        }.addTo(disposeBag)
    }

    private fun flashPeriodically() {
        // Flash red for 100ms every 4 musical ticks
        val now = musicalTimeSource.positionAndTempo.musicalTime(musicalTimeSource.systemTiming.now())
        val period = musicalTimeSource.positionAndTempo.systemTime(now + AppConfig.Timings.FLASH_MUSICAL_TICK) - musicalTimeSource.systemTiming.now()

        Observable.interval(0, period.toMillis(), TimeUnit.MILLISECONDS).subscribe {
            flashCircle()
        }.addTo(disposeBag)
    }

    private fun flashCircle() {
        // Flash red for 100ms
        binding.circleView.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
        Observable.timer(AppConfig.Timings.FLASH_DURATION, TimeUnit.MILLISECONDS).subscribe {
            binding.circleView.backgroundTintList =
                ContextCompat.getColorStateList(this@SyncedAudioPlayerActivity, R.color.white)
        }.addTo(disposeBag)
    }

    private fun displayAudioPlayItem(item: AudioPlayItem) {
        // show play item for duration of 32 musical ticks.
        val now = musicalTimeSource.positionAndTempo.musicalTime(musicalTimeSource.systemTiming.now())
        val delay = musicalTimeSource.positionAndTempo.systemTime(now + AppConfig.Timings.PLAY_ITEM_MUSICAL_TICK) - musicalTimeSource.systemTiming.now()
        Observable.timer(delay.toMillis(), TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe {
            queue.remove(item)
            playItemsAdapter.items = queue
        }.addTo(disposeBag)
    }
}