package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.DARK_THEME_ENABLED
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings


class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences) :
    SettingsRepository {

    private val sharedPrefsTheme = sharedPreferences.getBoolean(DARK_THEME_ENABLED, false)
    private val themeSettings = ThemeSettings(sharedPrefsTheme)
    override fun getThemeSettings(): Boolean {
        return themeSettings.isDarkThemeEnabled
    }

    override fun updateThemeSettings(isEnabled: Boolean) {
        themeSettings.isDarkThemeEnabled = isEnabled

        sharedPreferences.edit()
            .putBoolean(DARK_THEME_ENABLED, isEnabled)
            .apply()

        App().switchTheme(themeSettings.isDarkThemeEnabled)
    }
}