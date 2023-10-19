package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.player.di.playInteractorModule
import com.practicum.playlistmaker.player.di.playRepositoryModule
import com.practicum.playlistmaker.player.di.playViewModelModule
import com.practicum.playlistmaker.search.di.dataModule
import com.practicum.playlistmaker.search.di.interactorModule
import com.practicum.playlistmaker.search.di.repositoryModule
import com.practicum.playlistmaker.search.di.viewModelModule
import com.practicum.playlistmaker.settings.di.settingsRepositoryModule
import com.practicum.playlistmaker.settings.di.settingsViewModelModule
import com.practicum.playlistmaker.sharing.di.sharingDataModule
import com.practicum.playlistmaker.sharing.di.sharingInteractorModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val SHARED_PREFERENCES = "local_storage"
const val DARK_THEME_ENABLED = "false"

class App : Application() {
    var darkTheme = false;

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_ENABLED, darkTheme)
        switchTheme(darkTheme)

        startKoin {
            androidContext(this@App)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
            modules(playRepositoryModule, playInteractorModule, playViewModelModule)
            modules(sharingDataModule, sharingInteractorModule)
            modules(settingsRepositoryModule, settingsViewModelModule)
        }
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES

            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}