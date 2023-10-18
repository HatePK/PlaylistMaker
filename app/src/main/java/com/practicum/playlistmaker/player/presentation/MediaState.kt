package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface MediaState {
    data class Prepared(val data: Track): MediaState
    object Playing: MediaState
    object Paused: MediaState
}