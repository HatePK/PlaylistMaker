package com.practicum.playlistmaker.library.domain.impl

import com.practicum.playlistmaker.library.domain.FavouritesInteractor
import com.practicum.playlistmaker.library.domain.FavouritesRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouritesInteractorImpl(private val favouritesRepository: FavouritesRepository): FavouritesInteractor {
    override suspend fun addTrackToFavourites(track: Track) {
        favouritesRepository.addTrackToFavourites(track)
    }

    override suspend fun deleteTrackFromFavourites(track: Track) {
        favouritesRepository.deleteTrackFromFavourites(track)
    }

    override fun getFavourites(): Flow<List<Track>> {
        return favouritesRepository.getFavourites().map { it.reversed() }
    }

    override fun getFavouritesId(): Flow<List<String>> {
        return favouritesRepository.getFavouritesId()
    }
}