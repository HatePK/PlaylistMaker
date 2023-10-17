package com.practicum.playlistmaker.search.domain

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.domain.entity.Track

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
    fun addTrackToHistory(item: Track)
    fun returnSavedTracks():ArrayList<Track>
    fun clearSavedTracks()
}