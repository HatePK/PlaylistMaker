package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView


class TrackAdapter(val tracks: ArrayList<Track>, val onListElementClickListener: SearchActivity) : RecyclerView.Adapter<TrackViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            onListElementClickListener.addTrackToHistory(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}