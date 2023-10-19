package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.presentation.MediaViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playViewModelModule = module {

    viewModel { parameters -> MediaViewModel(track = parameters.get(), get()) }

}