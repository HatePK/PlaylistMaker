package com.practicum.playlistmaker.settings.di

import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsViewModelModule = module {

    viewModel {
        SettingsViewModel(get(), get())
    }

}