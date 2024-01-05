package com.practicum.playlistmaker.player.di

import com.practicum.playlistmaker.player.presentation.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playViewModelModule = module {

    viewModel { parameters -> PlayerViewModel(track = parameters.get(), get(), get(), get()) }

}