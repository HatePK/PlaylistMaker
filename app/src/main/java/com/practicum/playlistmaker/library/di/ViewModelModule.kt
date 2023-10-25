package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.presentation.FavouritesViewModel
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryModule = module {

    viewModel {
        FavouritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}