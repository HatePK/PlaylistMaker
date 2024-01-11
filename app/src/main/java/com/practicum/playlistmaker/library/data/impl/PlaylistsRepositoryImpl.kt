package com.practicum.playlistmaker.library.data.impl

import android.content.Context
import android.content.Intent
import com.practicum.playlistmaker.library.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.library.data.converters.PlaylistsTracksDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.PlaylistEntity
import com.practicum.playlistmaker.library.domain.PlaylistsRepository
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.utils.NumbersEnding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class PlaylistsRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val playlistsTracksDbConverter: PlaylistsTracksDbConverter,
    private val context: Context
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

    override suspend fun getPlaylistsTracks(tracksId: List<String>): Flow<Track> = flow {
        val allTracks = appDatabase.playlistsTracksDao().getPlaylistsTracks()
            .map { trackEntity -> playlistsTracksDbConverter.map(trackEntity) }

        tracksId.forEach{ id ->
            val track = allTracks.find {track: Track -> track.trackId == id}
            if (track != null) {
                emit(track)
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(trackId: String, playlist: Playlist) {

        val updatedTracksId = playlist.tracksId
        updatedTracksId.remove(trackId)
        val updatedTracksAmount = playlist.tracksAmount - 1

        val updatedPlaylist = playlist.copy(
            tracksId = updatedTracksId,
            tracksAmount = updatedTracksAmount
        )
        appDatabase.playlistDao().updatePlaylist(PlaylistDbConverter().map(updatedPlaylist))

        deleteTrackFromPlaylistsTracksDb(trackId)
    }

    override fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {

        var tracksStringList = ""

        tracks.forEach { track ->
            tracksStringList += "${tracks.indexOf(track) + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})\n"
        }

        val text = "${playlist.playlistName}\n" +
                "${playlist.playlistDescription}\n" +
                "${playlist.tracksAmount} ${NumbersEnding().edit("трек", playlist.tracksAmount)}\n" +
                "$tracksStringList"

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(shareIntent)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {

        val tracksId = playlist.tracksId

        withContext(Dispatchers.IO) {
            appDatabase.playlistDao().deletePlaylistEntity(PlaylistDbConverter().map(playlist))
        }

        tracksId.forEach{
            deleteTrackFromPlaylistsTracksDb(it)
        }
    }

    private suspend fun deleteTrackFromPlaylistsTracksDb(trackId: String) {

        val playlists = appDatabase.playlistDao().getPlaylists()
        val tracksId = ArrayList<String>()

        playlists.forEach{playlist ->
            tracksId.add(playlist.tracksId)
        }

        tracksId.toString()

        if (trackId !in tracksId) {
            withContext(Dispatchers.IO) {
                appDatabase.playlistsTracksDao().deleteTrackById(trackId.toLong())
            }
        }

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