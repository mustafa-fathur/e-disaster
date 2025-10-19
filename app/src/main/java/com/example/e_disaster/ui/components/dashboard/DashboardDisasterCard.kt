package com.example.e_disaster.ui.components.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.e_disaster.data.model.home.Disaster



@Composable
fun DisasterCard(disaster: Disaster, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.small
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(disaster.title, fontWeight = FontWeight.Bold)
            Text(disaster.location, style = MaterialTheme.typography.bodySmall)
            Text(
                text = disaster.date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}