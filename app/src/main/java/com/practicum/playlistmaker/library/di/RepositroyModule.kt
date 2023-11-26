package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.data.impl.FavouritesRepositoryImpl
import com.practicum.playlistmaker.library.domain.FavouritesRepository
import org.koin.dsl.module

val libraryRepositoryModule = module {

    single<FavouritesRepository> {
        FavouritesRepositoryImpl(get(), get())
    }

}