package com.practicum.playlistmaker.utils

import android.content.Context
import com.practicum.playlistmaker.player.data.impl.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.player.domain.MediaPlayerInteractor
import com.practicum.playlistmaker.search.domain.SearchInteractor
import com.practicum.playlistmaker.search.domain.SearchRepository
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.data.localStorage.SharedPreferencesLocalClient
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigationImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl

object Creator {
    private fun getTracksRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(
            RetrofitNetworkClient(context),
            SharedPreferencesLocalClient(
                context.getSharedPreferences(
                    "saved_tracks", Context.MODE_PRIVATE
                )
            )
        )
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigationImpl(context)
    }

    fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences("playlist_maker_preferences", Context.MODE_PRIVATE)
        )
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(getExternalNavigator(context))
    }

    fun provideTracksInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(getTracksRepository(context))
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(MediaPlayerRepositoryImpl())
    }

}