package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun DisasterReportDetailScreen(navController: NavController, reportId: String?) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Laporan",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate("update-disaster-report/$reportId")
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Ubah")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ReportHeaderCard(
                title = "Update Korban dan Kerusakan",
                description = "15 rumah rusak berat, 30 rumah rusak ringan. Total korban luka ringan bertambah menjadi 25 orang.",
                reporter = "Ahmad Wijaya",
                time = "2024-10-28 14:30",
                location = "-6.8167, 107.1464"
            )

            // Foto section title + action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Foto Laporan (2)", style = MaterialTheme.typography.titleSmall)
                TextButton(onClick = { /* navigate to gallery */ }) {
                    Text(text = "Tampilkan Semua", color = MaterialTheme.colorScheme.primary)
                }
            }

            // Photo previews (simulate thumbnails)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Large rounded image placeholder
                Box(
                    modifier = Modifier
                        .size(width = 220.dp, height = 160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2EAF3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "foto",
                        tint = Color(0xFFBDB2C9),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Right column with vertical thumbnails
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF2EAF3)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "foto kecil",
                            tint = Color(0xFFBDB2C9),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEEE5EF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "foto kecil",
                            tint = Color(0xFFBDB2C9),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Additional details or actions can go here
        }
    }
}

@Composable
private fun ColumnScope.ReportHeaderCard(
    title: String,
    description: String,
    reporter: String,
    time: String,
    location: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = title, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "!", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                }

                Text(text = description, style = MaterialTheme.typography.bodyMedium, maxLines = 3)

                // info rows
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Pelapor\n$reporter", style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Waktu Laporan\n$time", style = MaterialTheme.typography.bodySmall)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = "Koordinat Lokasi\n$location", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DisasterReportDetailPreview() {
    EDisasterTheme {
        // We can't provide a real NavController in preview, show only content by calling ReportHeaderCard inside a Column
        Column(modifier = Modifier.padding(16.dp)) {
            ReportHeaderCard(
                title = "Update Korban dan Kerusakan",
                description = "15 rumah rusak berat, 30 rumah rusak ringan. Total korban luka ringan bertambah menjadi 25 orang.",
                reporter = "Ahmad Wijaya",
                time = "2024-10-28 14:30",
                location = "-6.8167, 107.1464"
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF2EAF3)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "foto",
                        tint = Color(0xFFBDB2C9),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}
