package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.data.local.UserPreferences
import com.stdy4u.study4u.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _isFirstLaunch = MutableStateFlow<Boolean?>(null)
    val isFirstLaunch: StateFlow<Boolean?> = _isFirstLaunch.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.isFirstLaunch.collect { firstLaunch ->
                _isFirstLaunch.value = firstLaunch
            }
        }
    }

    fun completeOnboarding(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userPreferences.setOnboardingComplete(name)
            settingsRepository.insertSettings(
                com.stdy4u.study4u.domain.model.AppSettings(
                    primaryColorValue = null,
                    themeMode = com.stdy4u.study4u.domain.model.ThemeMode.SYSTEM,
                    notificationEnabled = true,
                    userName = name,
                    onboardingComplete = true,
                    pomodoroFocusDuration = 25,
                    pomodoroShortBreak = 5,
                    pomodoroLongBreak = 15
                )
            )
        }
    }
}
