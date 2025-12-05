package com.example.e_disaster.ui.features.disaster.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.badges.DisasterReportStatusBadge
import com.example.e_disaster.ui.features.disaster.detail.ReportItem
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard

@Composable
fun ReportsTabContent(navController: NavController, reports: List<ReportItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(reports, key = { it.id }) { report ->
            ListItemCard(
                onClick = { navController.navigate("disaster-report-detail/${report.id}") }
            ) {
                ReportCardContent(report = report)
            }
        }
    }
}

@Composable
private fun ReportCardContent(report: ReportItem) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Baris atas: Judul Laporan
        Text(
            text = report.title,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
            fontWeight = FontWeight.Bold
        )

        // Baris tengah: Deskripsi Laporan
        Text(
            text = "Tetap Waspada asdasdas das dasjkd bnsajkdbasjb dkasb kdjbnasjk d sada....", // Placeholder deskripsi
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Baris bawah: Pelapor, Waktu, dan Badge Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Informasi pelapor dan waktu
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Pelapor",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ahmad Wijaya • ${report.date}", // Menggabungkan nama dan waktu
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Badge Status di sisi kanan (Contoh: 'Final')
            // Logika ini hanya contoh, sesuaikan dengan data asli Anda
            if (report.id == "1") {
                Box {
                    DisasterReportStatusBadge(isCompleted = true)
                }
            }
        }
    }
}
