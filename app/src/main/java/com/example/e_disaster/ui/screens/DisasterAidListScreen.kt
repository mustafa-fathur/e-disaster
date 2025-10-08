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
fun DisasterAidListScreen(navController: NavController, disasterId: String?) {
    val aidId = "coba-coba-le-2028"
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Bantuan",
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
            Text("Ini halaman daftar bantuan bencana dengan ID bencana: $disasterId.")

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("add-disaster-aid/$disasterId")
            }) {
                Text("Tambah Bantuan Bencana")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                navController.navigate("update-disaster-aid/$aidId")
            }) {
                Text("Ubah Data Bantuan Bencana")
            }
        }
    }
}