package com.practicum.playlistmaker.library.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
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
    application: Application,
    private val playlist: Playlist,
    private val playlistsInteractor: PlaylistsInteractor): AndroidViewModel(application) {

    val tracks = ArrayList<Track>()

    private var _tracksDuration = MutableLiveData("0")
    val tracksDuration: LiveData<String> = _tracksDuration

    private var _tracksAmount = MutableLiveData(0)
    val tracksAmount: LiveData<Int> = _tracksAmount

    private var _playlistsState = MutableLiveData<PlaylistUnitState>(PlaylistUnitState.Loading)
    val playlistsState: LiveData<PlaylistUnitState> = _playlistsState

    private var _playlistInfo = MutableLiveData(playlist)
    val playlistInfo: LiveData<Playlist> = _playlistInfo

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
                _playlistInfo.postValue(it.filter { it.playlistId == playlist.playlistId }[0])
            }
        }
    }

    private suspend fun updateInfo() {

        val reversedTracks = ArrayList<Track>()

        playlistsInteractor.getPlaylistsTracks(playlist.tracksId).collect {dbTracks ->
            reversedTracks.add(dbTracks)
        }

        reversedTracks.reverse()
        tracks.addAll(reversedTracks)

        if (tracks.isEmpty()) {
            _playlistsState.postValue(PlaylistUnitState.Empty(getApplication<Application>().getString(R.string.playlist_unit_empty_placeholder)))
        } else {
            _playlistsState.postValue(PlaylistUnitState.Content(tracks))
        }

        var duration = 0
        tracks.forEach{ duration += it.trackTimeMillis.toInt() }

        val stringDuration = SimpleDateFormat("m", Locale.getDefault()).format(duration)

        _tracksDuration.postValue(stringDuration)
        _tracksAmount.postValue(tracks.size)
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