package com.stdy4u.study4u.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stdy4u.study4u.presentation.components.BubbleNavBar
import com.stdy4u.study4u.presentation.screen.*

object Routes {
    const val HOME = "home"
    const val TRACKER = "tracker"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val COURSE_DETAIL = "courseDetail/{courseId}"
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"

    fun courseDetail(courseId: String) = "courseDetail/$courseId"
}

val bottomNavItems = listOf(Routes.HOME, Routes.TRACKER, Routes.STATS)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(Routes.HOME, Routes.TRACKER, Routes.STATS)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(animationSpec = tween(300)) { it },
                exit = slideOutVertically(animationSpec = tween(300)) { it }
            ) {
                BubbleNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300)) { it / 4 }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                    onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } }
                )
            }

            composable(Routes.ONBOARDING) {
                FeaturePreviewScreen(
                    onComplete = { navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }
                )
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToCourseDetail = { courseId ->
                        navController.navigate(Routes.courseDetail(courseId))
                    },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }

            composable(Routes.TRACKER) {
                TrackerScreen()
            }

            composable(Routes.STATS) {
                StatsScreen(
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) }
                )
            }

            composable(
                route = Routes.COURSE_DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
                CourseDetailScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
