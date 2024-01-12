package com.practicum.playlistmaker.library.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourites_table")
data class TrackEntity(
    @PrimaryKey
    @ColumnInfo(name = "track_id")
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    val isFavorite: Boolean
)
