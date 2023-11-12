package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>
    fun addTrackToHistory(item: Track)
    fun returnSavedTracks():ArrayList<Track>
    fun clearSavedTracks()
}