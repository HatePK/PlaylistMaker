package com.practicum.playlistmaker.ui.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.data.dto.TrackDto
import com.practicum.playlistmaker.ui.search.CURRENT_TRACK
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var play: ImageButton
    private lateinit var timer: TextView

    companion object {
        private const val UPDATE_DEBOUNCE_DELAY = 500L
    }

    private var mediaPlayer = Creator.provideMediaPlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                    binding.timer.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition())
                    handler.postDelayed(this, UPDATE_DEBOUNCE_DELAY)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        play = findViewById(R.id.playButton)
        timer = findViewById(R.id.timer)

        intent?.let {
            val track = intent.extras?.getParcelable(CURRENT_TRACK) as TrackDto?
            addTrackToMedia(track!!)
            preparePlayer(track)
        }

        binding.menuButton.setOnClickListener {
            super.onBackPressed()
        }

        play.setOnClickListener {
            mediaPlayer.playbackControl(
                { startPlayer() },
                { pausePlayer() }
            )
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
    private fun addTrackToMedia(track: TrackDto) {
        binding.trackName.text = track.trackName
        binding.trackAuthor.text = track.artistName
        binding.trackDuration.text = track.getFormattedDuration()
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        binding.timer.text = "00:00"

        if (track.collectionName == "") {
            binding.albumGroup.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(8)
            )
            .into(binding.trackCover)
    }

    private fun preparePlayer(track: TrackDto) {
        mediaPlayer.preparePlayer(
            track.previewUrl,
            {
                play.setImageResource(R.drawable.play_button)
            },
            {
                handler.removeCallbacks(createUpdateTimerTask())
                play.setImageResource(R.drawable.play_button)
                binding.timer.text = "00:00"
            }
        )
    }

    private fun startPlayer() {
        mediaPlayer.startPlayer {
            play.setImageResource(R.drawable.pause_button)
            handler.post(
                createUpdateTimerTask()
            )
        }
    }

    private fun pausePlayer() {
        mediaPlayer.pausePlayer{
            play.setImageResource(R.drawable.play_button)
            handler.removeCallbacks(createUpdateTimerTask())
        }
    }
}