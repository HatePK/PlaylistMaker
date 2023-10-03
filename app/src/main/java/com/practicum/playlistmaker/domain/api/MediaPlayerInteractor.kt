package com.practicum.playlistmaker.domain.api


interface MediaPlayerInteractor {
    fun preparePlayer(
        dataSource: String,
        onPreparedPlayer: () -> Unit,
        onCompletionPlayer: () -> Unit
    )
    fun startPlayer(onStartPlayer: () -> Unit)
    fun pausePlayer(onPausePlayer: () -> Unit)
    fun playbackControl(onStartPlayer: () -> Unit, onPausePlayer: () -> Unit)
    fun release()
    fun currentPosition(): Int
}