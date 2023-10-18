package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.player.presentation.MediaState
import com.practicum.playlistmaker.player.presentation.MediaViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.CURRENT_TRACK

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var play: ImageButton
    private lateinit var timer: TextView
    private lateinit var viewModel: MediaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        play = findViewById(R.id.playButton)
        timer = findViewById(R.id.timer)

        intent?.let {
            val track = intent.extras?.getSerializable(CURRENT_TRACK) as Track
            viewModel = ViewModelProvider(this, MediaViewModel.getViewModelFactory(track))[MediaViewModel::class.java]

            preparePlayer(track)
        }

        viewModel.state.observe(this) { state ->
            render(state)
        }

        viewModel.getTimerLiveData().observe(this) { timer ->
            changeTimer(timer)
        }

        binding.menuButton.setOnClickListener {
            super.onBackPressed()
        }

        play.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun render(state: MediaState) {
        when (state) {
            is MediaState.Prepared -> preparePlayer(state.data)
            is MediaState.Playing -> showPlaying()
            is MediaState.Paused -> showPause()
        }
    }

    private fun changeTimer(timer: String) {
        binding.timer.text = timer
    }

    private fun showPlaying() {
        play.setImageResource(R.drawable.pause_button)
    }

    private fun showPause() {
        play.setImageResource(R.drawable.play_button)
    }
    private fun preparePlayer(track: Track) {
        binding.trackName.text = track.trackName
        binding.trackAuthor.text = track.artistName
        binding.trackDuration.text = track.trackTime
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country
        play.setImageResource(R.drawable.play_button)

        if (track.collectionName == "") {
            binding.albumGroup.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(8)
            )
            .into(binding.trackCover)
    }
}