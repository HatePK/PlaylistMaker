package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.domain.FavouritesInteractor
import com.practicum.playlistmaker.library.domain.impl.FavouritesInteractorImpl
import org.koin.dsl.module

val libraryInteractorModule = module {

    single<FavouritesInteractor> {
        FavouritesInteractorImpl(get())
    }

}