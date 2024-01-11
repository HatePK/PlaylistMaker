package com.practicum.playlistmaker.library.data.converters

import com.practicum.playlistmaker.library.data.db.PlaylistsTracksEntity
import com.practicum.playlistmaker.library.data.db.TrackEntity
import com.practicum.playlistmaker.search.domain.entity.Track

class PlaylistsTracksDbConverter  {
    fun map(track: Track): PlaylistsTracksEntity {
        return PlaylistsTracksEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }

    fun map(track: PlaylistsTracksEntity): Track {
        return Track(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.trackTime,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
            track.isFavorite
        )
    }
}