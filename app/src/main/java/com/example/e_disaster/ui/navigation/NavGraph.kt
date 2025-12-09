package com.example.e_disaster.ui.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.e_disaster.ui.features.auth.login.LoginScreen
import com.example.e_disaster.ui.features.auth.profile.ProfileScreen
import com.example.e_disaster.ui.features.auth.register.RegisterScreen
import com.example.e_disaster.ui.features.disaster.AddDisasterScreen
import com.example.e_disaster.ui.features.disaster.UpdateDisasterScreen
import com.example.e_disaster.ui.features.disaster.detail.DisasterDetailScreen
import com.example.e_disaster.ui.features.disaster.list.DisasterListScreen
import com.example.e_disaster.ui.features.disaster_aid.add.AddDisasterAidScreen
import com.example.e_disaster.ui.features.disaster_aid.detail.DisasterAidDetailScreen
import com.example.e_disaster.ui.features.disaster_aid.update.UpdateDisasterAidScreen
import com.example.e_disaster.ui.features.disaster_history.HistoryScreen
import com.example.e_disaster.ui.features.disaster_report.AddDisasterReportScreen
import com.example.e_disaster.ui.features.disaster_report.DisasterReportDetailScreen
import com.example.e_disaster.ui.features.disaster_report.UpdateDisasterReportScreen
import com.example.e_disaster.ui.features.disaster_victim.add.AddDisasterVictimScreen
import com.example.e_disaster.ui.features.disaster_victim.detail.DisasterVictimDetailScreen
import com.example.e_disaster.ui.features.disaster_victim.update.UpdateDisasterVictimScreen
import com.example.e_disaster.ui.features.home.HomeScreen
import com.example.e_disaster.ui.features.notification.NotificationScreen
import com.example.e_disaster.ui.features.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    intent: Intent
) {

    LaunchedEffect(key1 = intent) {
        handleNotificationIntent(intent, navController)
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
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
            "disaster-report-detail/{disasterId}/{reportId}",
            arguments = listOf(
                navArgument("disasterId") { type = NavType.StringType },
                navArgument("reportId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            val reportId = backStackEntry.arguments?.getString("reportId")
            DisasterReportDetailScreen(navController = navController, disasterId = disasterId, reportId = reportId)
        }

        composable(
            route = "update-disaster-report/{reportId}",
            arguments = listOf(navArgument("reportId") { type = NavType.StringType })
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId")
            UpdateDisasterReportScreen(navController = navController, reportId = reportId)
        }

        composable(
            route = "add-disaster-victim/{disasterId}",
            arguments = listOf(navArgument("disasterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            AddDisasterVictimScreen(navController = navController, disasterId = disasterId)
        }

        composable(
            route = "disaster-victim-detail/{disasterId}/{victimId}",
            arguments = listOf(
                navArgument("disasterId") { type = NavType.StringType },
                navArgument("victimId") { type = NavType.StringType }
                )
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            val victimId = backStackEntry.arguments?.getString("victimId")
            DisasterVictimDetailScreen(
                navController = navController,
                disasterId = disasterId,
                victimId = victimId
            )
        }

        composable(
            route = "update-disaster-victim/{disasterId}/{victimId}",
            arguments = listOf(
                navArgument("disasterId") { type = NavType.StringType },
                navArgument("victimId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val disasterId = backStackEntry.arguments?.getString("disasterId")
            val victimId = backStackEntry.arguments?.getString("victimId")
            UpdateDisasterVictimScreen(
                navController = navController,
                disasterId = disasterId,
                victimId = victimId
            )
        }

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

    }
}

private fun handleNotificationIntent(intent: Intent, navController: NavHostController) {
    if (intent.extras == null) return

    val notificationType = intent.getStringExtra("type")
    if (notificationType == "new_disaster_victim_report") {
        val disasterId = intent.getStringExtra("disaster_id")
        val victimId = intent.getStringExtra("victim_id")

        if (disasterId != null && victimId != null) {
            val route = "disaster-victim-detail/$disasterId/$victimId"
            navController.navigate(route)
        }
    }
    intent.removeExtra("type")
}
