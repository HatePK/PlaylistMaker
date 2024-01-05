package com.practicum.playlistmaker.library.domain.entity

import java.io.Serializable

data class Playlist (
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String,
    val playlistCoverUri: String,
    val tracksId: ArrayList<String>,
    var tracksAmount: Int
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Playlist)
            return false
        return playlistId == other.playlistId
    }
}
