package com.example.e_disaster.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppBottomNavBar
import com.example.e_disaster.ui.components.AppTopAppBar

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Beranda",
                onProfileClick = { navController.navigate("profile") }
            )
        },
        bottomBar = {
            AppBottomNavBar(
                onFabClick = { /* nanti */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Ini halaman beranda")
        }
    }
}