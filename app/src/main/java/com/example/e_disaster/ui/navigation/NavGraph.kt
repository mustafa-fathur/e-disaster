package com.example.e_disaster.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.screen.HomeScreen
import com.example.e_disaster.ui.screen.LoginScreen
import com.example.e_disaster.ui.screen.ProfileScreen
import com.example.e_disaster.ui.screen.RegisterScreen

@Composable
fun NavGraph() {
    // This is the core of the change: Setting up the navigation graph
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

        /* screen yg bakal ditambahin:
            Disaster List
            Add Disaster
            History
            Notification
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