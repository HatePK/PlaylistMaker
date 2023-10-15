package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.data.SearchRepository
import com.practicum.playlistmaker.search.domain.entity.Track
import java.util.concurrent.Executors

class SearchInteractorImpl(private val repository: SearchRepository): SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: SearchInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> { consumer.consume(resource.data, null) }
                is Resource.Error -> { consumer.consume(null, resource.message) }
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