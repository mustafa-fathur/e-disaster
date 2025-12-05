package com.example.e_disaster.ui.features.disaster.detail.contents

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.example.e_disaster.data.model.Disaster
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.e_disaster.R
import com.example.e_disaster.ui.features.disaster.detail.components.ImageSlider

@Composable
fun UnassignedDisasterContent(
    disaster: Disaster,
    onJoinClick: () -> Unit
) {
    val images = listOf(R.drawable.placeholder, R.drawable.placeholder, R.drawable.placeholder)

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        ImageSlider(images = images)
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(disaster.title ?: "Tanpa Judul", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(disaster.description ?: "Tidak ada deskripsi.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Anda belum bergabung menangani bencana ini. Bergabung untuk menambahkan laporan, data korban, dan bantuan.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onJoinClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Bergabung Menangani Bencana Ini")
                    }
                }
            }
        }
    }
}

@Composable
fun JoinConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Konfirmasi Penugasan") },
        text = { Text("Apakah Anda yakin ingin bergabung menangani bencana ini? Anda akan dapat menambahkan laporan, data korban, dan bantuan.") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Ya, Bergabung")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
