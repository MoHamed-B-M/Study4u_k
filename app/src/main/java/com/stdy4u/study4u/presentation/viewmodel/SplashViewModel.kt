package com.stdy4u.study4u.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _onboardingComplete = MutableStateFlow<Boolean?>(null)
    val onboardingComplete: StateFlow<Boolean?> = _onboardingComplete.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val settings = settingsRepository.getSettingsOnce()
            _onboardingComplete.value = settings?.onboardingComplete ?: false
        }
    }

    fun finishOnboarding() {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateOnboardingComplete(true)
        }
    }

    fun saveUserName(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.updateUserName(name)
        }
    }
}
