package com.practicum.playlistmaker.library.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor,
                         application: Application): AndroidViewModel(application) {

    private var playlistsState = MutableLiveData<PlaylistsState>(PlaylistsState.Loading)
    fun observePlaylistsState(): LiveData<PlaylistsState> = playlistsState
    init {
        updatePlaylists()
    }
    fun updatePlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { dbPlaylists ->
                    if (dbPlaylists.isEmpty()) {
                        playlistsState.postValue(
                            PlaylistsState.Empty(
                                getApplication<Application>()
                                    .getString(R.string.playlists_text_placeholder)
                            )
                        )
                    } else {
                        playlistsState.postValue(PlaylistsState.Content(dbPlaylists))
                    }
                }
        }
    }
}