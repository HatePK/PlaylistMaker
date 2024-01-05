package com.practicum.playlistmaker.library.presentation

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface FavouritesState {

    object Loading: FavouritesState
    data class Content(val favourites: List<Track>): FavouritesState
    data class Empty(val message: String): FavouritesState

}
