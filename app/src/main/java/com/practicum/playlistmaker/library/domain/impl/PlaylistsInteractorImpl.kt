package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository): PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        playlistsRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists().map { it.reversed() }
    }

    override suspend fun saveTrackInPlaylistsDb(track: Track) {
        playlistsRepository.saveTrackInPlaylistsDb(track)
    }

    override suspend fun getPlaylistsTracks(playlistsId: List<String>): Flow<Track> {
        return playlistsRepository.getPlaylistsTracks(playlistsId)
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlist: Playlist) {
        playlistsRepository.deleteTrackFromPlaylist(trackId, playlist)
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        playlistsRepository.sharePlaylist(playlist, tracks)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistsRepository.deletePlaylist(playlist)
    }
}