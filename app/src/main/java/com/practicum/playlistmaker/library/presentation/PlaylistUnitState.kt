package com.practicum.playlistmaker.library.presentation

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface PlaylistUnitState {
    object Loading: PlaylistUnitState
    object Empty: PlaylistUnitState
    data class Content(val tracks: List<Track>): PlaylistUnitState
}