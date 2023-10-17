package com.practicum.playlistmaker.settings.ui.view_model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.search.presentation.SearchViewModel
import com.practicum.playlistmaker.settings.data.SettingsRepository
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.utils.Creator

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

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    Creator.provideSharingInteractor(this[APPLICATION_KEY] as Application),
                    Creator.provideSettingsRepository(this[APPLICATION_KEY] as Application)
                )
            }
        }
    }
}