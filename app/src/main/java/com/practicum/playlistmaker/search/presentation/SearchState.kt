package com.practicum.playlistmaker.search.presentation
import com.practicum.playlistmaker.search.domain.entity.Track

sealed interface SearchState {

    object Loading : SearchState

    data class SearchContent(
        val tracks: ArrayList<Track>
    ) : SearchState

    data class SavedContent(
        val tracks: ArrayList<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String,
    ) : SearchState

    data class Empty(
        val message: String,
    ) : SearchState

}