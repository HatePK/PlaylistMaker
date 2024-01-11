package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun saveTrackInPlaylistsDb(track: Track)

    suspend fun getPlaylistsTracks(playlistsId: List<String>): Flow<Track>

    suspend fun deleteTrackFromPlaylist(trackId: String, playlist: Playlist)

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>)

    suspend fun deletePlaylist(playlist: Playlist)

}