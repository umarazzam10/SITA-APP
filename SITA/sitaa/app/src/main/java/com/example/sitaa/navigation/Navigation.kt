package com.example.sitaa.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sitaa.screen.splash.SplashScreen
import com.example.sitaa.screen.auth.LoginScreen
import com.example.sitaa.screen.home.HomeScreen
import com.example.sitaa.screen.thesis.list.ThesisListScreen
import com.example.sitaa.screen.thesis.detail.ThesisDetailScreen
import com.example.sitaa.screen.seminar.list.SeminarListScreen
import com.example.sitaa.screen.seminar.detail.SeminarDetailScreen
import com.example.sitaa.screen.defense.list.DefenseListScreen
import com.example.sitaa.screen.defense.detail.DefenseDetailScreen
import com.example.sitaa.screen.logbook.list.LogbookListScreen
import com.example.sitaa.screen.logbook.detail.LogbookDetailScreen
import com.example.sitaa.screen.notification.NotificationScreen
import com.example.sitaa.screen.profile.ProfileScreen
import com.example.sitaa.utils.Constants.Routes

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        // Splash Screen
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Home Screen
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToThesisList = {
                    navController.navigate(Routes.THESIS_LIST)
                },
                onNavigateToSeminarList = {
                    navController.navigate(Routes.SEMINAR_LIST)
                },
                onNavigateToDefenseList = {
                    navController.navigate(Routes.DEFENSE_LIST)
                },
                onNavigateToLogbookList = {
                    navController.navigate(Routes.LOGBOOK_LIST)
                },
                onNavigateToNotification = {
                    navController.navigate(Routes.NOTIFICATION)
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE)
                }
            )
        }

        // Thesis Screens
        composable(Routes.THESIS_LIST) {
            ThesisListScreen(
                onNavigateToDetail = { thesisId ->
                    navController.navigate(Routes.THESIS_DETAIL.replace("{thesisId}", thesisId.toString()))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Routes.THESIS_DETAIL,
            arguments = listOf(
                navArgument("thesisId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val thesisId = backStackEntry.arguments?.getInt("thesisId") ?: return@composable
            ThesisDetailScreen(
                thesisId = thesisId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Seminar Screens
        composable(Routes.SEMINAR_LIST) {
            SeminarListScreen(
                onNavigateToDetail = { seminarId ->
                    navController.navigate(Routes.SEMINAR_DETAIL.replace("{seminarId}", seminarId.toString()))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Routes.SEMINAR_DETAIL,
            arguments = listOf(
                navArgument("seminarId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val seminarId = backStackEntry.arguments?.getInt("seminarId") ?: return@composable
            SeminarDetailScreen(
                seminarId = seminarId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Defense Screens
        composable(Routes.DEFENSE_LIST) {
            DefenseListScreen(
                onNavigateToDetail = { defenseId ->
                    navController.navigate(Routes.DEFENSE_DETAIL.replace("{defenseId}", defenseId.toString()))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Routes.DEFENSE_DETAIL,
            arguments = listOf(
                navArgument("defenseId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val defenseId = backStackEntry.arguments?.getInt("defenseId") ?: return@composable
            DefenseDetailScreen(
                defenseId = defenseId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Logbook Screens
        composable(Routes.LOGBOOK_LIST) {
            LogbookListScreen(
                onNavigateToDetail = { studentId ->
                    navController.navigate(Routes.LOGBOOK_DETAIL.replace("{studentId}", studentId.toString()))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable(
            route = Routes.LOGBOOK_DETAIL,
            arguments = listOf(
                navArgument("studentId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: return@composable
            LogbookDetailScreen(
                studentId = studentId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Notification Screen
        composable(Routes.NOTIFICATION) {
            NotificationScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Profile Screen
        composable(Routes.PROFILE) {
            ProfileScreen(
                onNavigateBack = {
                    navController.navigateUp()
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}