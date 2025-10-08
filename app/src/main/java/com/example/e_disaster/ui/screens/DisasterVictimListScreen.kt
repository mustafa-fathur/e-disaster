package com.example.e_disaster.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppTopAppBar

@Composable
fun DisasterVictimListScreen(navController: NavController, disasterId: String?) {
    val victimId = "coba-coba-le-2027"
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ini halaman daftar korban bencana dengan ID bencana: $disasterId.")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("add-disaster-victim/$disasterId")
            }) {
                Text("Tambah Korban Bencana")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("disaster-victim-detail/$victimId")
            }) {
                Text("Detail Korban Bencana")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("update-disaster-victim/$victimId")
            }) {
                Text("Ubah Data Korban Bencana")
            }
        }
    }
}