package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.utils.Constants.BASE_URL
import com.example.e_disaster.data.model.ReportPicture

@Composable
fun DisasterReportDetailScreen(navController: NavController, disasterId: String?, reportId: String?) {
    val viewModel: DisasterReportDetailViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Laporan",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { navController.navigate("update-disaster-report/$disasterId/$reportId") }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                uiState.errorMessage != null -> Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
                uiState.report != null -> uiState.report.let { r ->
                    ReportInfoCard(
                        title = r.title,
                        description = r.description,
                        reporter = r.reporterName,
                        time = r.createdAt,
                        location = r.disasterTitle
                    )
                    PhotoSection(pictures = r.pictures ?: emptyList())
                }
            }
        }
    }
}

@Composable
private fun ReportInfoCard(
    title: String,
    description: String,
    reporter: String,
    time: String,
    location: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Title and Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = "Report Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Description
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            // Detail List
            DetailItemWithIcon(icon = Icons.Default.Person, label = "Pelapor", value = reporter)
            DetailItemWithIcon(icon = Icons.Default.CalendarMonth, label = "Waktu Laporan", value = time)
            DetailItemWithIcon(icon = Icons.Default.LocationOn, label = "Lokasi/Keterangan Bencana", value = location)
        }
    }
}

@Composable
private fun DetailItemWithIcon(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PhotoSection(pictures: List<ReportPicture>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Foto Laporan",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "Foto Laporan (${pictures.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(pictures.size) { idx ->
                val pic = pictures[idx]
                Card(
                    modifier = Modifier.size(140.dp, 120.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = "${BASE_URL}${pic.url}",
                        contentDescription = pic.caption ?: "Foto Laporan",
                        modifier = Modifier.fillMaxSize(),
                        placeholder = null,
                        error = null
                    )
                }
            }
        }
        TextButton(
            onClick = { /* TODO: Navigate to photo gallery screen */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Tampilkan Semua", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(showBackground = true, name = "Detail Report Light")
@Composable
fun DisasterReportDetailScreenLightPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterReportDetailScreen(navController = rememberNavController(), disasterId = "d1", reportId = "r1")
    }
}

@Preview(showBackground = true, name = "Detail Report Dark")
@Composable
fun DisasterReportDetailScreenDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterReportDetailScreen(navController = rememberNavController(), disasterId = "d1", reportId = "r1")
    }
}
