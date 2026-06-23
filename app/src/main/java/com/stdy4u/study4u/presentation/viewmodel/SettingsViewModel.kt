package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stdy4u.study4u.domain.model.*
import com.stdy4u.study4u.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: Int = 0,
    val selectedColorValue: Int? = null,
    val notificationEnabled: Boolean = true,
    val userName: String = "",
    val pomodoroFocusDuration: Int = 25,
    val pomodoroShortBreak: Int = 5,
    val pomodoroLongBreak: Int = 15,
    val appVersion: String = "1.0.0",
    val isCheckingUpdate: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                if (settings != null) {
                    _uiState.update {
                        it.copy(
                            themeMode = settings.themeMode.value,
                            selectedColorValue = settings.primaryColorValue,
                            notificationEnabled = settings.notificationEnabled,
                            userName = settings.userName,
                            pomodoroFocusDuration = settings.pomodoroFocusDuration,
                            pomodoroShortBreak = settings.pomodoroShortBreak,
                            pomodoroLongBreak = settings.pomodoroLongBreak
                        )
                    }
                }
            }
        }
    }

    fun updateThemeMode(mode: Int) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(mode)
            _uiState.update { it.copy(themeMode = mode) }
        }
    }

    fun updatePrimaryColor(colorValue: Int) {
        viewModelScope.launch {
            settingsRepository.updatePrimaryColor(colorValue)
            _uiState.update { it.copy(selectedColorValue = colorValue) }
        }
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationEnabled(enabled)
            _uiState.update { it.copy(notificationEnabled = enabled) }
        }
    }

    fun updateUserName(name: String) {
        viewModelScope.launch {
            settingsRepository.updateUserName(name)
            _uiState.update { it.copy(userName = name) }
        }
    }

    fun updatePomodoroSettings(focus: Int, shortBreak: Int, longBreak: Int) {
        viewModelScope.launch {
            settingsRepository.updatePomodoroSettings(focus, shortBreak, longBreak)
            _uiState.update {
                it.copy(
                    pomodoroFocusDuration = focus,
                    pomodoroShortBreak = shortBreak,
                    pomodoroLongBreak = longBreak
                )
            }
            _toastMessage.emit("Pomodoro settings updated")
        }
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCheckingUpdate = true) }
            // Github API check would go here
            kotlinx.coroutines.delay(2000)
            _uiState.update { it.copy(isCheckingUpdate = false) }
            _toastMessage.emit("You have the latest version")
        }
    }
}
