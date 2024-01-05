package com.practicum.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.utils.NumbersEnding

class PlaylistSmallViewHolder(view: View): RecyclerView.ViewHolder(view) {

    private val cover: ImageView = itemView.findViewById(R.id.playlistSmallCover)
    private val title: TextView = itemView.findViewById(R.id.playlistSmallName)
    private val description: TextView = itemView.findViewById(R.id.playlistSmallDescription)

    fun bind(playlist: Playlist) {

        val tracksAmount = playlist.tracksAmount
        val editedWord = NumbersEnding().edit("трек", tracksAmount)

        title.text = playlist.playlistName
        description.text = "$tracksAmount $editedWord"

        Glide.with(itemView)
            .load(playlist.playlistCoverUri)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(8)
            )
            .into(cover)
    }
}