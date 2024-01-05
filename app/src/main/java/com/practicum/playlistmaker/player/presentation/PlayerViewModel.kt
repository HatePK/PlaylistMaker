package com.practicum.playlistmaker.player.presentation

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.FavouritesInteractor
import com.practicum.playlistmaker.library.domain.PlaylistsInteractor
import com.practicum.playlistmaker.library.domain.entity.Playlist
import com.practicum.playlistmaker.library.presentation.FavouritesState
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val favouritesInteractor: FavouritesInteractor,
    private val playlistsInteractor: PlaylistsInteractor,
): ViewModel() {

    private var timerJob: Job? = null
    private var playlists = ArrayList<Playlist>()

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val observePlayerState: LiveData<PlayerState> = playerState

    private val isFavourite = MutableLiveData(track.isFavorite)
    val observeIsFavourite: LiveData<Boolean> = isFavourite

    private val playlistsLiveData = MutableLiveData(playlists)
    val observePlaylistsLiveData: LiveData<ArrayList<Playlist>> = playlistsLiveData

    private var toastMessage = MutableLiveData<String>()
    val observerToastMessage: LiveData<String> = toastMessage

    init {
        mediaPlayerInteractor.preparePlayer(
            track.previewUrl,
            { playerState.postValue(PlayerState.Prepared()) },
            { playerState.postValue(PlayerState.Prepared()) }
        )

        mediaPlayerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        }

        loadPlaylists()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun loadPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists()
                .collect { loadedPlaylists ->
                    playlists.addAll(loadedPlaylists)
                    playlistsLiveData.postValue(playlists)
                }
        }
    }
    fun updatePlaylists() {
        playlists.clear()
        loadPlaylists()
    }
    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        if (playlist.tracksId.contains(track.trackId)) {
            toastMessage.postValue("Трек уже добавлен в плейлист ${playlist.playlistName}")
        } else {
            viewModelScope.launch {
                val updatedPlaylist = playlist.apply {
                    this.tracksId.add(track.trackId)
                    this.tracksAmount += 1
                }
                playlistsInteractor.updatePlaylist(updatedPlaylist)
                playlistsInteractor.saveTrackInPlaylistsDb(track)
                toastMessage.postValue("Добавлено в плейлист ${playlist.playlistName}")
            }
        }
    }

    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(
            startPlayer(), pausePlayer()
        )
    }

    fun onFavoriteClicked() {
        if (isFavourite.value == false) {
            track.isFavorite = true
            viewModelScope.launch {
                Log.d("ABOBA", "$track")
                favouritesInteractor.addTrackToFavourites(track)
            }
            isFavourite.postValue(true)
        } else {
            track.isFavorite = false
            viewModelScope.launch {
                Log.d("ABOBA", "$track")
                favouritesInteractor.deleteTrackFromFavourites(track)
            }
            isFavourite.postValue(false)
        }
    }

    private fun startPlayer(): () -> Unit =  {
        mediaPlayerInteractor.startPlayer {
            playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            startTimer()
        }
    }

    private fun pausePlayer(): () -> Unit =  {
        mediaPlayerInteractor.pausePlayer {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Paused(getCurrentPlayerPosition()))
        }
    }

    private fun releasePlayer() {
        mediaPlayerInteractor.release()
        playerState.value = PlayerState.Default()
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayerInteractor.isPlaying()) {
                delay(DEBOUNCE_DELAY_MILLIS)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayerInteractor.currentPosition()) ?: TIMER
    }

    companion object {
        private const val DEBOUNCE_DELAY_MILLIS = 300L
        private const val TIMER = "00:00"
    }
}