package com.practicum.playlistmaker.player.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.FavouritesInteractor
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
    private val favouritesInteractor: FavouritesInteractor
): ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val observePlayerState: LiveData<PlayerState> = playerState

    private val isFavourite = MutableLiveData(track.isFavorite)
    val observeIsFavourite: LiveData<Boolean> = isFavourite

    init {
        viewModelScope.launch {
            favouritesInteractor.getFavouritesId().collect {tracksId ->
                Log.d("ABOBA", "$tracksId")
                if (tracksId.contains(track.trackId)) {
                    isFavourite.postValue(true)
                } else {
                    isFavourite.postValue(false)
                }
            }
        }

        mediaPlayerInteractor.preparePlayer(
            track.previewUrl,
            { playerState.postValue(PlayerState.Prepared()) },
            { playerState.postValue(PlayerState.Prepared()) }
        )

        mediaPlayerInteractor.setOnCompletionListener {
            timerJob?.cancel()
            playerState.postValue(PlayerState.Prepared())
        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(
            startPlayer(), pausePlayer()
        )
    }

    fun onFavoriteClicked() {
        if (isFavourite.value == false) {
            viewModelScope.launch {
                favouritesInteractor.addTrackToFavourites(track)
            }
            isFavourite.postValue(true)
        } else {
            viewModelScope.launch {
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