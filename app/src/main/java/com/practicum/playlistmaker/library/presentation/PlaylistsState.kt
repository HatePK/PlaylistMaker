package com.practicum.playlistmaker.library.presentation

import com.practicum.playlistmaker.library.domain.entity.Playlist

sealed interface PlaylistsState {

    object Loading: PlaylistsState

    data class Content(val favourites: List<Playlist>): PlaylistsState
    data class Empty(val message: String): PlaylistsState

}