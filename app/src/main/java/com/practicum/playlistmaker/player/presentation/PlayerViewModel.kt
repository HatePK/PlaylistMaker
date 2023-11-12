package com.practicum.playlistmaker.player.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val track: Track,
    private val mediaPlayerInteractor: MediaPlayerInteractor
): ViewModel() {

    private var timerJob: Job? = null

    private val playerState = MutableLiveData<PlayerState>(PlayerState.Default())
    val observePlayerState: LiveData<PlayerState> = playerState

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
                delay(debounceDelay)
                playerState.postValue(PlayerState.Playing(getCurrentPlayerPosition()))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayerInteractor.currentPosition()) ?: timer
    }

    companion object {
        private const val debounceDelay = 300L
        private const val timer = "00:00"
    }
}