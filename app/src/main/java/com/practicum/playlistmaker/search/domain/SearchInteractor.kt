package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entity.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)
    fun addTrackToHistory(item: Track)
    fun returnSavedTracks():ArrayList<Track>
    fun clearSavedTracks()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}