package com.practicum.playlistmaker.search.presentation

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.entity.Track

class SearchViewModel (application: Application, private val searchInteractor: SearchInteractor) : AndroidViewModel(application) {

        private val handler = Handler(Looper.getMainLooper())

        private val tracks = ArrayList<Track>()
        private val savedTracks = searchInteractor.returnSavedTracks()

        private val stateLiveData = MutableLiveData<SearchState>()
        private val savedTracksLiveData = MutableLiveData(savedTracks)
        fun observeState(): LiveData<SearchState> = stateLiveData
        fun getSavedTracksLiveData(): LiveData<ArrayList<Track>> = savedTracksLiveData

        private fun renderState(state: SearchState) {
            stateLiveData.postValue(state)
        }

        init {
            renderState(SearchState.SavedContent(savedTracks))
        }

        fun searchDebounce(changedText: String) {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

            val searchRunnable = Runnable { makeSearch(changedText) }
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

            handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
        }

        fun makeSearch(newSearchText: String) {
            if (newSearchText.isNotEmpty()) {

                renderState(SearchState.Loading)

                searchInteractor.searchTracks(newSearchText, object : SearchInteractor.TracksConsumer {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
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
                })
            }
        }

        override fun onCleared() {
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        }

        fun clearHistory(){
            searchInteractor.clearSavedTracks()
            savedTracks.clear()
            stateLiveData.value = SearchState.SavedContent(savedTracks)
        }

        fun clearTracks(){
            handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

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
            val sharedPrefsTracks = searchInteractor.returnSavedTracks()

            savedTracks.clear()
            savedTracks.addAll(sharedPrefsTracks)

            savedTracksLiveData.postValue(sharedPrefsTracks)
        }

        companion object {
            private const val SEARCH_DEBOUNCE_DELAY = 2000L
            private val SEARCH_REQUEST_TOKEN = Any()
        }
}