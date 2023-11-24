package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import com.practicum.playlistmaker.player.presentation.PlayerState
import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.CURRENT_TRACK
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("DEPRECATION")
class PlayerActivity : AppCompatActivity() {
    private var binding: ActivityMediaBinding? = null
    private lateinit var playButton: ImageButton
    private lateinit var timer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        playButton = findViewById(R.id.playButton)
        timer = findViewById(R.id.timer)

        intent?.let {
            val track = intent.extras?.getSerializable(CURRENT_TRACK) as Track
            val viewModel by viewModel<PlayerViewModel> { parametersOf(track) }

            preparePlayer(track)

            viewModel.observePlayerState.observe(this) {
                playButton.isEnabled = it.isPlayButtonEnabled
                timer.text = it.progress

                playButton.setImageResource(playButtonImage(it.isPlaying))
            }

            viewModel.observeIsFavourite.observe(this) {isFavourite ->
                if (isFavourite) {
                    binding?.likeButton?.setImageResource(R.drawable.like_button_active)
                } else {
                    binding?.likeButton?.setImageResource(R.drawable.like_button)
                }
            }

            binding?.menuButton?.setOnClickListener {
                super.onBackPressed()
            }

            binding?.likeButton?.setOnClickListener {
                viewModel.onFavoriteClicked()
            }

            playButton.setOnClickListener {
                viewModel.playbackControl()
            }
        }
    }
    private fun playButtonImage(isPlaying: Boolean): Int {
        return if (isPlaying) {
             R.drawable.pause_button
        } else {
            R.drawable.play_button
        }
    }
    private fun preparePlayer(track: Track) {
        binding?.trackName?.text = track.trackName
        binding?.trackAuthor?.text = track.artistName
        binding?.trackDuration?.text = track.trackTime
        binding?.trackAlbum?.text = track.collectionName
        binding?.trackYear?.text = track.releaseDate.substring(0, 4)
        binding?.trackGenre?.text = track.primaryGenreName
        binding?.trackCountry?.text = track.country
        playButton.setImageResource(R.drawable.play_button)

        if (track.collectionName == "") {
            binding?.albumGroup?.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(8)
            )
            .into(binding?.trackCover!!)
    }
}