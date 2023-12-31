package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.Track


class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val image: ImageView = itemView.findViewById(R.id.artworkUrl100)
    private val name: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val time: TextView = itemView.findViewById(R.id.trackTime)

    fun bind(model: Track) {
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(2F, itemView.context))
            )
            .into(image)

        name.text = model.trackName
        artistName.text = model.artistName
        time.text = model.trackTime
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}