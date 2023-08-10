package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.ActivityMediaBinding
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            val track = intent.extras?.getParcelable(CURRENT_TRACK) as Track?
            addTrackToMedia(track!!)
        }

        binding.menuButton.setOnClickListener {
            super.onBackPressed()
        }
    }
    private fun addTrackToMedia(track: Track) {
        binding.trackName.text = track.trackName
        binding.trackAuthor.text = track.artistName
        binding.trackDuration.text = track.getFormattedDuration()
        binding.trackAlbum.text = track.collectionName
        binding.trackYear.text = track.releaseDate.substring(0, 4)
        binding.trackGenre.text = track.primaryGenreName
        binding.trackCountry.text = track.country

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
}