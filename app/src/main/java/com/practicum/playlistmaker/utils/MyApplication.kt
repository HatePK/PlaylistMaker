package com.practicum.playlistmaker.utils

import android.app.Application
import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl

class MyApplication: Application() {
    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(MediaPlayerRepositoryImpl())
    }
}