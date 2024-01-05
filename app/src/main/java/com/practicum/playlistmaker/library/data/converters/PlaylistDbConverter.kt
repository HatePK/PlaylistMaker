package com.practicum.playlistmaker.library.data.converters

import com.google.gson.Gson
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.domain.entity.Playlist

class PlaylistDbConverter {

    private val gson = Gson()

    fun map(playlist: Playlist): PlaylistEntity {

        val tracksId = gson.toJson(playlist.tracksId)

        return PlaylistEntity(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverUri,
            tracksId,
            playlist.tracksAmount
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {

        var tracksId = gson.fromJson(playlist.tracksId, ArrayList<String>()::class.java)

        if (tracksId == null) {
            tracksId = ArrayList<String>()
        }

        return Playlist(
            playlist.playlistId,
            playlist.playlistName,
            playlist.playlistDescription,
            playlist.playlistCoverUri,
            tracksId,
            playlist.tracksAmount
        )
    }
}