package com.practicum.playlistmaker.search.di

import android.content.Context
import com.google.gson.Gson
import com.practicum.playlistmaker.SHARED_PREFERENCES
import com.practicum.playlistmaker.search.data.LocalClient
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.localStorage.SharedPreferencesLocalClient
import com.practicum.playlistmaker.search.data.network.AppleApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val APPLE_API_URL = "https://itunes.apple.com"

val dataModule = module {

    single<AppleApi> {
        Retrofit.Builder()
            .baseUrl(APPLE_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppleApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<LocalClient> {
        SharedPreferencesLocalClient(get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

}