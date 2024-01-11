package com.practicum.playlistmaker.library.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.entity.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlist: Playlist,
    private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    fun editPlaylist(
        name: String,
        description: String,
        coverUri: String,
    ) {
        viewModelScope.launch {
            val updatedPlaylist = playlist.copy(
                playlistName = name,
                playlistDescription = description,
                playlistCoverUri = coverUri
            )

            playlistsInteractor.updatePlaylist(updatedPlaylist)
        }
    }

}