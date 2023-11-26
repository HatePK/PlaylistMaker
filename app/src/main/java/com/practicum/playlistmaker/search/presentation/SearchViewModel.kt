package com.practicum.playlistmaker.search.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.library.domain.FavouritesInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.utils.Debounce
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class SearchViewModel (
    application: Application,
    private val searchInteractor: SearchInteractor,
    private val favouritesInteractor: FavouritesInteractor
) : AndroidViewModel(application) {


        private val tracks = ArrayList<Track>()
        private val savedTracks = ArrayList<Track>()

        private val stateLiveData = MutableLiveData<SearchState>()
        fun observeState(): LiveData<SearchState> = stateLiveData


        private val savedTracksLiveData = MutableLiveData<ArrayList<Track>>()
        fun getSavedTracksLiveData(): LiveData<ArrayList<Track>> = savedTracksLiveData


        private var latestSearchText: String? = null

        init {
            viewModelScope.launch {
                savedTracks.addAll(searchInteractor.returnSavedTracks())
            }
        }

        fun compare() {
            viewModelScope.launch {
                favouritesInteractor
                    .getFavouritesId()
                    .collect { idList ->
                        for (track in tracks) {
                            track.isFavorite = idList.contains(track.trackId)
                        }
                        for (track in savedTracks) {
                            track.isFavorite = idList.contains(track.trackId)
                        }
                    }
            }
        }

        private fun renderState(state: SearchState) {
            stateLiveData.postValue(state)
        }

        private val debounceHandler = Debounce()

        private val trackSearchDebounce = debounceHandler.debounce<String>(
            SEARCH_DEBOUNCE_DELAY,
            viewModelScope,
            true,
        ) {
            makeSearch(it)
        }
        fun searchDebounce(changedText: String) {
            if (latestSearchText != changedText) {
                latestSearchText = changedText
                trackSearchDebounce(changedText)
            }
        }

        fun makeSearch(newSearchText: String) {
            if (newSearchText.isNotEmpty()) {

                renderState(SearchState.Loading)

                viewModelScope.launch {
                    searchInteractor.searchTracks(newSearchText)
                        .collect { pair ->
                            processResult(pair.first, pair.second)
                        }
                }
            }
        }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        if (foundTracks != null) {
            tracks.clear()
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(
                    SearchState.Error(errorMessage = getApplication<Application>().getString(R.string.something_went_wrong))
                )
            }

            tracks.isEmpty() -> {
                renderState(
                    SearchState.Empty(message = getApplication<Application>().getString(R.string.nothing_found))
                )
            }

            else -> {
                renderState(
                    SearchState.SearchContent(tracks = tracks)
                )
            }
        }
    }

        fun clearHistory(){
            searchInteractor.clearSavedTracks()
            savedTracks.clear()
            stateLiveData.value = SearchState.SavedContent(savedTracks)
        }

        fun clearTracks(){
            debounceHandler.cancel()

            tracks.clear()
            stateLiveData.value = SearchState.SearchContent(tracks)
        }

        fun showHistoryTracks() {
            if (savedTracks.size > 0) {
                renderState(SearchState.SavedContent(tracks = savedTracks))
            }
        }

        fun addTrackToHistory(track: Track) {
            searchInteractor.addTrackToHistory(track)

            viewModelScope.launch {
                val sharedPrefsTracks = searchInteractor.returnSavedTracks()

                savedTracks.clear()
                savedTracks.addAll(sharedPrefsTracks)

                savedTracksLiveData.postValue(sharedPrefsTracks)
            }
        }

        companion object {
            private const val SEARCH_DEBOUNCE_DELAY = 2000L
        }
}