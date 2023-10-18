package com.practicum.playlistmaker
import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.ui.activity.DARK_THEME_ENABLED
import com.practicum.playlistmaker.settings.ui.activity.SHARED_PREFERENCES

class App : Application() {
    var darkTheme = false;

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_ENABLED, darkTheme)
        switchTheme(darkTheme)
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