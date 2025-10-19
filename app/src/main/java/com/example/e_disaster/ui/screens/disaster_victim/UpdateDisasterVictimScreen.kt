package com.example.e_disaster.ui.screens.disaster_victim

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppTopAppBar

@Composable
fun UpdateDisasterVictimScreen(navController: NavController, victimId: String?) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Update Laporan Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // TODO: Fetch existing victim data using victimId and pass as initial values
            VictimForm(
                buttonText = "Ubah",
                onFormSubmit = { nik, name, age, description ->
                    // TODO: Implement ViewModel logic to update victim
                    println("Updating Victim ID $victimId: NIK=$nik, Name=$name, Age=$age, Desc=$description")
                    navController.popBackStack()
                }
            )
        }
    }
}
