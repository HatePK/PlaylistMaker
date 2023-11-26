package com.practicum.playlistmaker.library.data.impl

import com.practicum.playlistmaker.library.data.converters.TrackDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.data.db.TrackEntity
import com.practicum.playlistmaker.library.domain.FavouritesRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavouritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): FavouritesRepository {
    override suspend fun addTrackToFavourites(track: Track) {
        saveTrack(track)
    }

    override suspend fun deleteTrackFromFavourites(track: Track) {
        deleteTrack(track)
    }

    override fun getFavourites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getFavouriteTracks()
        emit(convertFromTrackEntity(tracks))
    }

    override fun getFavouritesId(): Flow<List<String>> = flow {
        val tracksId = appDatabase.trackDao().getFavouritesId()
        emit(tracksId)
    }

    private suspend fun saveTrack(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    private suspend fun deleteTrack(track: Track) {
        val trackEntity = trackDbConverter.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map {track -> trackDbConverter.map(track)}
    }
}