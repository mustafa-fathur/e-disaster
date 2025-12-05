package com.example.e_disaster.ui.features.disaster.detail.tabs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.e_disaster.R
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.ui.features.disaster.detail.components.ImageSlider

@Composable
fun IdentityTabContent(disaster: Disaster) {
    val images = listOf(R.drawable.placeholder, R.drawable.placeholder, R.drawable.placeholder)
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        ImageSlider(images = images)
        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(disaster.title ?: "Tanpa Judul", style = MaterialTheme.typography.titleLarge)
                    Text(disaster.description ?: "Tidak ada deskripsi.", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}