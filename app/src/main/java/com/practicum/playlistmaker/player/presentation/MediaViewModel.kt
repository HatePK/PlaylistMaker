package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.entity.Track
import com.practicum.playlistmaker.utils.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class MediaViewModel(
    private val track: Track,
    private val mediaPlayerInteractor: MediaPlayerInteractor
): ViewModel() {
    private val _state = MutableLiveData<MediaState>()
    val state: LiveData<MediaState> = _state

    private val debounceDelay = 500L
    var timer = "00:00"

    private val timerLiveData = MutableLiveData(timer)

    fun getTimerLiveData(): LiveData<String> = timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    init {
        mediaPlayerInteractor.preparePlayer(
            track.previewUrl,
            {
                _state.postValue(MediaState.Prepared(track))
            },
            {
                handler.removeCallbacksAndMessages(createUpdateTimerTask())
                _state.postValue(MediaState.Prepared(track))

                timer = "00:00"
                timerLiveData.postValue(timer)
            }
        )
    }

    override fun onCleared() {
        mediaPlayerInteractor.release()
        handler.removeCallbacksAndMessages(null)
    }

    fun playbackControl() {
        mediaPlayerInteractor.playbackControl(
            startPlayer(), pausePlayer()
        )
    }

    private fun startPlayer(): () -> Unit =  {
        mediaPlayerInteractor.startPlayer {
            handler.post(createUpdateTimerTask())
            _state.postValue(MediaState.Playing)
        }
    }

    private fun pausePlayer(): () -> Unit =  {
        mediaPlayerInteractor.pausePlayer {
            handler.removeCallbacks(createUpdateTimerTask())
            _state.postValue(MediaState.Paused)
        }
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                timer = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayerInteractor.currentPosition())
                timerLiveData.postValue(timer)
                handler.postDelayed(this, debounceDelay)
            }
        }
    }

    companion object {
        fun getViewModelFactory(track: Track): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MediaViewModel(
                    track,
                    Creator.provideMediaPlayerInteractor()
                )
            }
        }
    }
}