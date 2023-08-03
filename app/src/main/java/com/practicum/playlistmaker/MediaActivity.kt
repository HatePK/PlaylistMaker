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
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION")
class MediaActivity : AppCompatActivity() {

    private lateinit var trackCover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackAuthor: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var albumGroup: androidx.constraintlayout.widget.Group

    private lateinit var addCollectionButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var timer: TextView
    private lateinit var backButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media)
        val trackJson = intent.getStringExtra("track");
        val track = trackFromJson(trackJson)

        setViews()
        addTrackToMedia(track)

        backButton.setOnClickListener {
            super.onBackPressed()
        }
    }
    private fun setViews() {
        backButton = findViewById(R.id.menu_button)
        trackCover = findViewById(R.id.trackCover)
        trackName = findViewById(R.id.trackName)
        trackAuthor = findViewById(R.id.trackAuthor)
        trackDuration = findViewById(R.id.trackDuration)
        trackAlbum = findViewById(R.id.trackAlbum)
        trackYear = findViewById(R.id.trackYear)
        trackGenre = findViewById(R.id.trackGenre)
        trackCountry = findViewById(R.id.trackCountry)
        addCollectionButton = findViewById(R.id.addCollectionButton)
        playButton = findViewById(R.id.playButton)
        likeButton = findViewById(R.id.likeButton)
        timer = findViewById(R.id.timer)
        albumGroup = findViewById(R.id.albumGroup)
    }

    private fun addTrackToMedia(track: Track) {
        val formattedDuration = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis.toInt())

        trackName.text = track.trackName
        trackAuthor.text = track.artistName
        trackDuration.text = formattedDuration
        trackAlbum.text = track.collectionName
        trackYear.text = track.releaseDate.substring(0, 4)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country

        if (track.collectionName == "") {
            albumGroup.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
            )
            .into(trackCover)
    }
    private fun trackFromJson (string: String?) : Track {
        val gson = Gson()
        return gson.fromJson(string, Track::class.java)
    }
}