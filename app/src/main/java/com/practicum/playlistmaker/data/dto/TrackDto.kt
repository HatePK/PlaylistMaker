package com.practicum.playlistmaker.data.dto

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getFormattedDuration() = SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeMillis.toInt())
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackId)
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeString(trackTimeMillis)
        parcel.writeString(artworkUrl100)
        parcel.writeString(collectionName)
        parcel.writeString(releaseDate)
        parcel.writeString(primaryGenreName)
        parcel.writeString(country)
        parcel.writeString(previewUrl)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TrackDto> {
        override fun createFromParcel(parcel: Parcel): TrackDto {
            return TrackDto(parcel)
        }

        override fun newArray(size: Int): Array<TrackDto?> {
            return arrayOfNulls(size)
        }
    }
}




