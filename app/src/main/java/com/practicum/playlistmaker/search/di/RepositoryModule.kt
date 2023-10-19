package com.practicum.playlistmaker.search.di

import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.domain.SearchRepository
import org.koin.dsl.module

val repositoryModule = module {

    single <SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

}