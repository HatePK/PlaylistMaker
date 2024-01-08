package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.ui.TrackAdapter

class PlaylistSmallAdapter(val clickListener: PlaylistClickListener): RecyclerView.Adapter<PlaylistSmallViewHolder>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSmallViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_small_view, parent, false)
        return PlaylistSmallViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistSmallViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlists[position])
        }
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}