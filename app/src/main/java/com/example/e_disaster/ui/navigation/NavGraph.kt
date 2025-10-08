package com.example.e_disaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_disaster.ui.screen.AddDisasterScreen
import com.example.e_disaster.ui.screen.DisasterDetailScreen
import com.example.e_disaster.ui.screen.DisasterListScreen
import com.example.e_disaster.ui.screen.HistoryScreen
import com.example.e_disaster.ui.screen.HomeScreen
import com.example.e_disaster.ui.screen.LoginScreen
import com.example.e_disaster.ui.screen.NotificationScreen
import com.example.e_disaster.ui.screen.ProfileScreen
import com.example.e_disaster.ui.screen.RegisterScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("register") {
            RegisterScreen(navController = navController)
        }

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("profile"){
            ProfileScreen(navController = navController)
        }

        composable("disaster-list") {
            DisasterListScreen(navController = navController)
        }

        composable("add-disaster") {
            AddDisasterScreen(navController = navController)
        }

        composable("history") {
            HistoryScreen(navController = navController)
        }

        composable("notification") {
            NotificationScreen(navController = navController)
        }

        composable(
            "disaster-detail/{disasterId}",
            arguments = listOf(navArgument("disasterId") {type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            DisasterDetailScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "update-disaster/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call UpdateDisasterScreen(navController, disasterId)
        }

        composable(
            route = "add-disaster-report/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call AddDisasterReportScreen(navController, disasterId)
        }

        composable(
            route = "update-disaster-report/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")
            // TODO: Create and call UpdateDisasterReportScreen(navController, reportId)
        }

        composable(
            route = "disaster-victim-list/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call DisasterVictimListScreen(navController, disasterId)
        }

        composable(
            route = "add-disaster-victim/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call AddDisasterVictimScreen(navController, disasterId)
        }

        composable(
            route = "disaster-victim-detail/{victimId}",
            arguments = listOf(navArgument("victimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val victimId = backStackEntry.arguments?.getString("victimId")
            // TODO: Create and call DisasterVictimDetailScreen(navController, victimId)
        }

        composable(
            route = "update-disaster-victim/{victimId}",
            arguments = listOf(navArgument("victimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val victimId = backStackEntry.arguments?.getString("victimId")
            // TODO: Create and call UpdateDisasterVictimScreen(navController, victimId)
        }

        composable(
            route = "disaster-aid-list/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call DisasterAidListScreen(navController, disasterId)
        }

        composable(
            route = "add-disaster-aid/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            // TODO: Create and call AddDisasterAidScreen(navController, disasterId)
        }

        composable(
            route = "update-disaster-aid/{aidId}",
            arguments = listOf(navArgument("aidId") { type = NavType.StringType })
        ) { backStackEntry ->
            val aidId = backStackEntry.arguments?.getString("aidId")
            // TODO: Create and call UpdateDisasterAidScreen(navController, aidId)
        }

    }
}