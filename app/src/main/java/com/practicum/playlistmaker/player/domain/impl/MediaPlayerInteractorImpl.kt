package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository):
    MediaPlayerInteractor {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT

    override fun preparePlayer(
        dataSource: String,
        onPreparedPlayer: () -> Unit,
        onCompletionPlayer: () -> Unit
    ) {
        mediaPlayerRepository.setDataSource(dataSource)
        mediaPlayerRepository.prepareAsync()
        mediaPlayerRepository.setOnPreparedListener {
            onPreparedPlayer()
            playerState = STATE_PREPARED
        }
        mediaPlayerRepository.setOnCompletionListener {
            onCompletionPlayer()
            playerState = STATE_PREPARED
        }
    }

    override fun startPlayer(startPlayer: () -> Unit) {
        mediaPlayerRepository.start()
        playerState = STATE_PLAYING
        startPlayer()
    }

    override fun pausePlayer(pausePlayer: () -> Unit) {
        mediaPlayerRepository.pause()
        playerState = STATE_PAUSED
        pausePlayer()
    }

    override fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(onPausePlayer)
            }
            STATE_PAUSED, STATE_PREPARED -> {
                startPlayer(onStartPlayer)
            }
        }
    }

    override fun release() {
        mediaPlayerRepository.release()
    }

    override fun currentPosition(): Int {
        return mediaPlayerRepository.currentPosition()
    }
}