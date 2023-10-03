package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.TrackDto


class TrackAdapter(private val tracks: ArrayList<TrackDto>, private val onListElementClickListener: SearchActivity) : RecyclerView.Adapter<TrackViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onListElementClickListener.goToMedia(tracks[position])
            onListElementClickListener.addTrackToHistory(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}