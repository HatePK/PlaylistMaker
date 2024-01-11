package com.practicum.playlistmaker.library.presentation

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface PlaylistUnitState {
    object Loading: PlaylistUnitState
    data class Content(val tracks: List<Track>): PlaylistUnitState
    data class Empty(val message: String): PlaylistUnitState
}