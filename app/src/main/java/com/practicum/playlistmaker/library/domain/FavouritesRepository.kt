package com.practicum.playlistmaker.library.domain

import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface FavouritesRepository {

    suspend fun addTrackToFavourites(track: Track)
    suspend fun deleteTrackFromFavourites(track: Track)
    fun getFavourites(): Flow<List<Track>>
    fun getFavouritesId(): Flow<List<String>>

}