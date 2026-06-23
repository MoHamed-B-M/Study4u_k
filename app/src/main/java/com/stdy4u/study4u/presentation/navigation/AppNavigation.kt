package com.stdy4u.study4u.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.stdy4u.study4u.presentation.components.FloatingNavBar
import com.stdy4u.study4u.presentation.components.UserNameDialog
import com.stdy4u.study4u.presentation.screen.*
import com.stdy4u.study4u.presentation.viewmodel.SplashViewModel

private val bottomNavRoutes = Screen.bottomNavItems.map { it.route }

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomNavRoutes
    val navigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SPLASH,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                fadeIn(animationSpec = tween(300)) +
                        slideInHorizontally(animationSpec = tween(300)) { it / 4 }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            }
        ) {
            composable(Screen.SPLASH) {
                SplashScreen(
                    onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.SPLASH) { inclusive = true } } },
                    onNavigateToOnboarding = { navController.navigate(Screen.MORPHING_ONBOARDING) { popUpTo(Screen.SPLASH) { inclusive = true } } }
                )
            }

            composable(Screen.ONBOARDING) {
                FeaturePreviewScreen(
                    onComplete = { navController.navigate(Screen.Home.route) { popUpTo(Screen.ONBOARDING) { inclusive = true } } }
                )
            }

            composable(Screen.MORPHING_ONBOARDING) {
                val splashVm: SplashViewModel = hiltViewModel()
                var showNameDialog by remember { mutableStateOf(false) }

                if (showNameDialog) {
                    UserNameDialog(
                        onConfirm = { name ->
                            splashVm.saveUserName(name)
                            showNameDialog = false
                            navController.navigate(Screen.Home.route) { popUpTo(Screen.MORPHING_ONBOARDING) { inclusive = true } }
                        }
                    )
                }

                MorphingOnboardingScreen(
                    onComplete = {
                        splashVm.finishOnboarding()
                        showNameDialog = true
                    }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToCourseDetail = { courseId ->
                        navController.navigate(Screen.courseDetail(courseId))
                    },
                    onNavigateToSettings = { navController.navigate(Screen.SETTINGS) }
                )
            }

            composable(Screen.Tracker.route) {
                TrackerScreen()
            }

            composable(Screen.Stats.route) {
                StatsScreen(
                    onNavigateToSettings = { navController.navigate(Screen.SETTINGS) }
                )
            }

            composable(
                route = Screen.COURSE_DETAIL,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId") ?: return@composable
                CourseDetailScreen(
                    courseId = courseId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.SETTINGS) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }

        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically(animationSpec = tween(300)) { it },
            exit = slideOutVertically(animationSpec = tween(300)) { it },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            FloatingNavBar(
                currentRoute = currentRoute,
                onNavigate = navigate
            )
        }
    }
}
