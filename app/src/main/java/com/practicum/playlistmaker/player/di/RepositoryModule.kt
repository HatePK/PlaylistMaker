package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.MediaPlayerRepository
import org.koin.dsl.module

val playRepositoryModule = module {

    single<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl()
    }

}