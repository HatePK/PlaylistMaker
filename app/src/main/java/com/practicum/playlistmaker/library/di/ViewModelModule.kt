package com.practicum.playlistmaker.library.di

import com.practicum.playlistmaker.library.presentation.AddNewPlaylistViewModel
import com.practicum.playlistmaker.library.presentation.EditPlaylistViewModel
import com.practicum.playlistmaker.library.presentation.FavouritesViewModel
import com.practicum.playlistmaker.library.presentation.PlaylistUnitViewModel
import com.practicum.playlistmaker.library.presentation.PlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val libraryViewModelModule = module {

    viewModel {
        FavouritesViewModel(get(), get())
    }

    viewModel {
        PlaylistsViewModel(get(), get())
    }

    viewModel {
        AddNewPlaylistViewModel(get())
    }

    viewModel{
        PlaylistUnitViewModel(get(), get())
    }

    viewModel{
        EditPlaylistViewModel(get(), get())
    }

}