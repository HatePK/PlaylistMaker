package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.entity.Track
import org.koin.dsl.module

val playInteractorModule = module {

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(get())
    }

}