package com.example.e_disaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.e_disaster.ui.features.Welcome
import com.example.e_disaster.ui.features.disaster_aid.AddDisasterAidScreen
import com.example.e_disaster.ui.features.disaster_report.AddDisasterReportScreen
import com.example.e_disaster.ui.features.disaster.AddDisasterScreen
import com.example.e_disaster.ui.features.disaster_victim.AddDisasterVictimScreen
//import com.example.e_disaster.ui.features.disaster_aid.DisasterAidListScreen
import com.example.e_disaster.ui.features.disaster.DisasterDetailScreen
import com.example.e_disaster.ui.features.disaster.DisasterListScreen
import com.example.e_disaster.ui.features.disaster_victim.DisasterVictimDetailScreen
import com.example.e_disaster.ui.features.disaster_victim.DisasterVictimListScreen
import com.example.e_disaster.ui.features.disaster_history.HistoryDetailScreen
import com.example.e_disaster.ui.features.disaster_history.HistoryScreen
import com.example.e_disaster.ui.features.home.HomeScreen
import com.example.e_disaster.ui.features.auth.login.LoginScreen
//import com.example.e_disaster.ui.features.disaster_aid.NearbyAidsScreen
import com.example.e_disaster.ui.features.notification.NotificationScreen
import com.example.e_disaster.ui.features.auth.profile.ProfileScreen
import com.example.e_disaster.ui.features.auth.RegisterScreen
import com.example.e_disaster.ui.features.disaster_aid.UpdateDisasterAidScreen
import com.example.e_disaster.ui.features.disaster_report.UpdateDisasterReportScreen
import com.example.e_disaster.ui.features.disaster.UpdateDisasterScreen
import com.example.e_disaster.ui.features.disaster_aid.DisasterAidDetailScreen
import com.example.e_disaster.ui.features.disaster_report.DisasterReportDetailScreen
import com.example.e_disaster.ui.features.disaster_victim.UpdateDisasterVictimScreen
import kotlinx.coroutines.delay

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            Welcome()
            LaunchedEffect(Unit) {
                delay(3000) // 3 seconds delay
                navController.navigate("login") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }

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
            route = "history-detail/{historyId}",
            arguments = listOf(navArgument("historyId") { type = NavType.StringType })
        ) { backStackEntry ->
            val historyId = backStackEntry.arguments?.getString("historyId")
            HistoryDetailScreen(navController = navController, historyId = historyId)
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
            "disaster-report-detail/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")
            DisasterReportDetailScreen(navController = navController, reportId = reportId)
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

//        composable(
//            route = "disaster-aid-list/{disasterId}",
//            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val disasterId = backStackEntry.arguments?.getString("disasterId")
//            DisasterAidListScreen(navController = navController, disasterId = disasterId)
//        }

        composable(
            route = "add-disaster-aid/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            AddDisasterAidScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            "disaster-aid-detail/{aidId}",
            arguments = listOf(navArgument("aidId") { type = NavType.StringType })
        ) { backStackEntry ->
            val aidId = backStackEntry.arguments?.getString("aidId")
            DisasterAidDetailScreen(navController = navController, aidId = aidId)
        }

        composable(
            route = "update-disaster-aid/{aidId}",
            arguments = listOf(navArgument("aidId") { type = NavType.StringType })
        ) { backStackEntry ->
            val aidId = backStackEntry.arguments?.getString("aidId")
            UpdateDisasterAidScreen(navController = navController, aidId = aidId)
        }

//        composable(
//            route = "nearby-aids/{latitude}/{longitude}",
//            arguments = listOf(
//                navArgument("latitude") { type = NavType.StringType },
//                navArgument("longitude") { type = NavType.StringType }
//            )
//        ) { backStackEntry ->
//            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
//            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
//            NearbyAidsScreen(
//                navController = navController,
//                userLatitude = latitude,
//                userLongitude = longitude
//            )
//        }

    }
}
