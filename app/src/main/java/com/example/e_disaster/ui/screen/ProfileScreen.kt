package com.example.e_disaster.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppTopAppBar


@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Profil",
                canNavigateBack = true, // This is a detail screen
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ini halaman profil.")
        }
    }
}