package com.practicum.playlistmaker.library.di

import androidx.room.Room
import com.practicum.playlistmaker.library.data.converters.TrackDbConverter
import com.practicum.playlistmaker.library.data.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val libraryDataModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    factory {TrackDbConverter()}
}