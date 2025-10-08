package com.example.e_disaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.screen.AddDisasterScreen
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

        /* screen yg bakal ditambahin:
            Disaster Detail
            Update Disaster
            Add Disaster Report
            Update Disaster Report

            Disaster Report map?

            Disaster Victim List
            Disaster Victim Detail
            Add Disaster Victim
            Update Disaster Victim

            Disaster Aid List
            Add Disaster Aid
            Update Disaster Aid
        * */
    }
}