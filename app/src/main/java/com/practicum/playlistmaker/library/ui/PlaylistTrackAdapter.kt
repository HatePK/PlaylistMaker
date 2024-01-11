package com.practicum.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.search.ui.TrackViewHolder

class PlaylistTrackAdapter(val clickListener: TrackClickListener, val onLongClickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder>()  {

    var tracks = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(tracks[position])
        }

        holder.itemView.setOnLongClickListener {
            onLongClickListener.onTrackClick(tracks[position])
            true
        }
    }

    override fun getItemCount(): Int = tracks.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}