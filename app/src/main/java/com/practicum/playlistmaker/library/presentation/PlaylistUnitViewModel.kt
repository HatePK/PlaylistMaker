package com.practicum.playlistmaker.library.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistUnitViewModel(
    private val playlist: Playlist,
    private val playlistsInteractor: PlaylistsInteractor): ViewModel() {

    val tracks = ArrayList<Track>()

    private var tracksDuration = MutableLiveData("0")
    fun observeTracksDuration(): LiveData<String> = tracksDuration

    private var tracksAmount = MutableLiveData(0)
    fun observeTracksAmount(): LiveData<Int> = tracksAmount

    private var playlistsState = MutableLiveData<PlaylistUnitState>(PlaylistUnitState.Loading)
    fun observePlaylistsState(): LiveData<PlaylistUnitState> = playlistsState

    private var playlistInfo = MutableLiveData(playlist)
    fun observePlaylistInfo(): LiveData<Playlist> = playlistInfo

    fun deleteTrack(trackId: String) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlist)
        }

        tracks.clear()

        viewModelScope.launch {
            updateInfo()
        }
    }

    init {
        viewModelScope.launch {
            updateInfo()
        }
    }

    fun updateAfterEdit() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                playlistInfo.postValue(it.filter { it.playlistId == playlist.playlistId }[0])
            }
        }
    }

    private suspend fun updateInfo() {
        playlistsInteractor.getPlaylistsTracks(playlist.tracksId).collect {dbTracks ->
            tracks.add(dbTracks)
        }

        if (tracks.isEmpty()) {
            playlistsState.postValue(PlaylistUnitState.Empty)
        } else {
            playlistsState.postValue(PlaylistUnitState.Content(tracks))
        }

        var duration = 0
        tracks.forEach{ duration += it.trackTimeMillis.toInt() }

        val stringDuration = SimpleDateFormat("m", Locale.getDefault()).format(duration)

        tracksDuration.postValue(stringDuration)
        tracksAmount.postValue(tracks.size)
    }

    fun sharePlaylist() {
        playlistsInteractor.sharePlaylist(playlist, tracks)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlist)
        }
    }

}