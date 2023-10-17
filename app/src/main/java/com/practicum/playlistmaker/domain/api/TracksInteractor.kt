package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.entity.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }
}