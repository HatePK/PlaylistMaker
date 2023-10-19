package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.presentation.SearchViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(androidApplication(), get())
    }
}