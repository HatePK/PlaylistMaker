package com.practicum.playlistmaker.library.data.impl

import com.practicum.playlistmaker.library.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.converters.PlaylistsTracksDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistsRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistsTracksDbConverter: PlaylistsTracksDbConverter
): PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        savePlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        updatePlaylistEntity(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(convertFromPlaylistEntity(playlists))
    }

    override suspend fun saveTrackInPlaylistsDb(track: Track) {
        val playlistsTrackEntity = playlistsTracksDbConverter.map(track)
        appDatabase.playlistsTracksDao().insertTrack(playlistsTrackEntity)
    }

    private suspend fun savePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().insertPlaylist(playlistEntity)
    }

    private suspend fun updatePlaylistEntity(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        appDatabase.playlistDao().updatePlaylist(playlistEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {playlist -> playlistDbConverter.map(playlist)}
    }
}