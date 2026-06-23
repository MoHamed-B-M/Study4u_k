package com.stdy4u.study4u.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Tracker : Screen("tracker", "Tracker", Icons.Default.TrackChanges)
    data object Stats : Screen("stats", "Stats", Icons.Default.Leaderboard)

    companion object {
        const val SETTINGS = "settings"
        const val COURSE_DETAIL = "courseDetail/{courseId}"
        const val SPLASH = "splash"
        const val ONBOARDING = "onboarding"

        fun courseDetail(courseId: String) = "courseDetail/$courseId"

        val bottomNavItems = listOf(Home, Tracker, Stats)
    }
}
