package com.example.e_disaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_disaster.ui.screens.AddDisasterAidScreen
import com.example.e_disaster.ui.screens.AddDisasterReportScreen
import com.example.e_disaster.ui.screens.AddDisasterScreen
import com.example.e_disaster.ui.screens.AddDisasterVictimScreen
import com.example.e_disaster.ui.screens.DisasterAidListScreen
import com.example.e_disaster.ui.screens.DisasterDetailScreen
import com.example.e_disaster.ui.screens.DisasterListScreen
import com.example.e_disaster.ui.screens.DisasterVictimDetailScreen
import com.example.e_disaster.ui.screens.DisasterVictimListScreen
import com.example.e_disaster.ui.screens.HistoryScreen
import com.example.e_disaster.ui.screens.HomeScreen
import com.example.e_disaster.ui.screens.LoginScreen
import com.example.e_disaster.ui.screens.NotificationScreen
import com.example.e_disaster.ui.screens.ProfileScreen
import com.example.e_disaster.ui.screens.RegisterScreen
import com.example.e_disaster.ui.screens.UpdateDisasterAidScreen
import com.example.e_disaster.ui.screens.UpdateDisasterReportScreen
import com.example.e_disaster.ui.screens.UpdateDisasterScreen
import com.example.e_disaster.ui.screens.UpdateDisasterVictimScreen

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

        // Semua route di bawah ini harusnya cuman bisa diakses oleh volunteer nanti.

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
            UpdateDisasterScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "add-disaster-report/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            AddDisasterReportScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "update-disaster-report/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")
            UpdateDisasterReportScreen(navController = navController, reportId = reportId)
        }

        composable(
            route = "disaster-victim-list/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            DisasterVictimListScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "add-disaster-victim/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            AddDisasterVictimScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "disaster-victim-detail/{victimId}",
            arguments = listOf(navArgument("victimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val victimId = backStackEntry.arguments?.getString("victimId")
            DisasterVictimDetailScreen(navController = navController, victimId = victimId)
        }

        composable(
            route = "update-disaster-victim/{victimId}",
            arguments = listOf(navArgument("victimId") { type = NavType.StringType })
        ) { backStackEntry ->
            val victimId = backStackEntry.arguments?.getString("victimId")
            UpdateDisasterVictimScreen(navController = navController, victimId = victimId)
        }

        composable(
            route = "disaster-aid-list/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            DisasterAidListScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "add-disaster-aid/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            AddDisasterAidScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "update-disaster-aid/{aidId}",
            arguments = listOf(navArgument("aidId") { type = NavType.StringType })
        ) { backStackEntry ->
            val aidId = backStackEntry.arguments?.getString("aidId")
            UpdateDisasterAidScreen(navController = navController, aidId = aidId)
        }

    }
}