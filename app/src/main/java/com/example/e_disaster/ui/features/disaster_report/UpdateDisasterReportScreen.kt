package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar

@Composable
fun UpdateDisasterReportScreen(navController: NavController, reportId: String?) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Ubah Perkembangan Bencana",
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
            Text("Ini halaman ubah data perkembangan bencana dengan ID laporan perkembangan: $reportId.")
        }
    }
}