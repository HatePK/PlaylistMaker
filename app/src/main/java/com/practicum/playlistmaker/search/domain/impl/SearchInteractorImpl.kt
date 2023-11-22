package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val repository: SearchRepository): SearchInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addTrackToHistory(item: Track) {
        repository.addTrackToHistory(item)
    }

    override fun clearSavedTracks() {
        repository.clearSavedTracks()
    }

    override fun returnSavedTracks(): ArrayList<Track> {
        return repository.returnSavedTracks()
    }
}