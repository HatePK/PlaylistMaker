package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings

const val DARK_THEME_ENABLED = "false"

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences): SettingsRepository {

    private val sharedPrefsTheme = sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(sharedPrefsTheme)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        App().switchTheme(settings.isDarkThemeEnabled)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_ENABLED, settings.isDarkThemeEnabled)
            .apply()
    }
}