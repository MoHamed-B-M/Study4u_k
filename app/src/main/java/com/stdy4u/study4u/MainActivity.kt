package com.stdy4u.study4u

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.stdy4u.study4u.domain.model.ThemeMode
import com.stdy4u.study4u.domain.repository.SettingsRepository
import com.stdy4u.study4u.presentation.navigation.AppNavigation
import com.stdy4u.study4u.presentation.theme.AccentColors
import com.stdy4u.study4u.presentation.theme.Study4UTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsRepository.getSettings().collectAsState(initial = null)
            val themeMode = settings?.themeMode?.value ?: ThemeMode.SYSTEM.value
            val seedColorValue = settings?.primaryColorValue
            val seedColor = seedColorValue?.let { colorValue ->
                AccentColors.find { it.hashCode() == colorValue }
            }

            Study4UTheme(
                themeMode = themeMode,
                seedColor = seedColor
            ) {
                AppNavigation()
            }
        }
    }
}
