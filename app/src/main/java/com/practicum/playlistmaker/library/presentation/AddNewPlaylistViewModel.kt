package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.entity.Playlist
import kotlinx.coroutines.launch

class AddNewPlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    fun addPlaylist(
        name: String,
        description: String = "",
        coverUri: String = "",
    ) {
        viewModelScope.launch {
            val playlist = Playlist(
                playlistName = name,
                playlistDescription = description,
                playlistCoverUri = coverUri,
                tracksId = ArrayList<String>(),
                tracksAmount = 0
            )

            playlistsInteractor.addPlaylist(playlist)
        }
    }

}