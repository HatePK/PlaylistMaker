package com.practicum.playlistmaker.library.presentation

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.FavouritesInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val favouritesInteractor: FavouritesInteractor,
    application: Application
): AndroidViewModel(application){

    private var favouritesState = MutableLiveData<FavouritesState>(FavouritesState.Loading)
    fun observeFavouritesState(): LiveData<FavouritesState> = favouritesState
    init {
        updateFavourites()
    }
    fun updateFavourites() {
        viewModelScope.launch {
            favouritesInteractor.getFavourites()
                .collect { dbTracks ->
                    if (dbTracks.isEmpty()) {
                        favouritesState.postValue(
                            FavouritesState.Empty(
                                getApplication<Application>()
                                .getString(R.string.favourites_text_placeholder)
                            )
                        )
                    } else {
                        favouritesState.postValue(FavouritesState.Content(dbTracks))
                    }
                }
        }
    }

}