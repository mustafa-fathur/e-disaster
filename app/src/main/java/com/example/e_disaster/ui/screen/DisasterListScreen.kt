package com.example.e_disaster.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.e_disaster.ui.components.AppBottomNavBar
import com.example.e_disaster.ui.components.AppTopAppBar


@Composable
fun DisasterListScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Bencana",
                onProfileClick = { navController.navigate("profile") }
            )
        },
        bottomBar = {
            AppBottomNavBar(navController = navController as NavHostController)
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Ini halaman daftar bencana")
            }
        }
    )
}