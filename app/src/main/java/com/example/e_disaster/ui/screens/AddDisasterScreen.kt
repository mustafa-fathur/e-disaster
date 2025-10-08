package com.example.e_disaster.ui.screens

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
fun AddDisasterScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Ini halaman tambah bencana baru.")
        }
    }
}