package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
    fun addTrackToHistory(item: Track)
    suspend fun returnSavedTracks():ArrayList<Track>
    fun clearSavedTracks()
}