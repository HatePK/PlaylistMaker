package com.practicum.playlistmaker.library.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverUri: String,
    val tracksId: String,
    val tracksAmount: Int,
)
