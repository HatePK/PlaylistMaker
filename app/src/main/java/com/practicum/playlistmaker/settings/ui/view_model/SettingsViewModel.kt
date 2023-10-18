package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsRepository: SettingsRepository,
): ViewModel() {

    var currentThemeStatus = settingsRepository.getThemeSettings().isDarkThemeEnabled
    private val currentThemeStatusLiveData = MutableLiveData(currentThemeStatus)

    fun getThemeLiveData(): LiveData<Boolean> = currentThemeStatusLiveData

    fun switchTheme(isChecked: Boolean) {
        settingsRepository.updateThemeSettings(ThemeSettings(isChecked))
        currentThemeStatus = settingsRepository.getThemeSettings().isDarkThemeEnabled
    }

    fun share(){
        sharingInteractor.shareApp()
    }

    fun support(){
        sharingInteractor.openSupport()
    }

    fun agreement() {
        sharingInteractor.openTerms()
    }

}