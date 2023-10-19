package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsRepository: SettingsRepository,
): ViewModel() {

    var currentThemeStatus = settingsRepository.getThemeSettings()

    fun switchTheme(isChecked: Boolean) {
        settingsRepository.updateThemeSettings(isChecked)
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