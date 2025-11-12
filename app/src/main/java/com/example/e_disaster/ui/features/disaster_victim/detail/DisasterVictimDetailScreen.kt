package com.example.e_disaster.ui.features.disaster_victim.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.badges.DisasterEvacuationStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterVictimStatusBadge
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster.VictimItem
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun DisasterVictimDetailScreen(navController: NavController, victimId: String?) {
    // Dummy data source mimicking the one from VictimsTab.kt and DisasterDetailScreen.kt
    val dummyVictims = listOf(
        VictimItem("1", "Siti Rahayu", "Luka ringan di kaki kanan akibat reruntuhan", "minor_injury", true),
        VictimItem("2", "Budi Santoso", "Mengalami dehidrasi", "minor_injury", false),
        VictimItem("3", "Ahmad Subarjo", "Patah tulang di lengan kiri", "serious_injuries", true)
    )

    // Find the victim by ID, or use the first one as a fallback for preview
    val victim = remember(victimId) {
        dummyVictims.find { it.id == victimId } ?: dummyVictims.first()
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { navController.navigate("update-disaster-victim/${victim.id}") }) {
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
            VictimInfoCard(victim = victim)
            PhotoSection()
        }
    }
}

@Composable
private fun VictimInfoCard(victim: VictimItem) {
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
            // Header: Name and Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = victim.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    DisasterVictimStatusBadge(status = victim.status)
                }
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Victim Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Details Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "NIK", value = "123131321321321321")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(label = "Tanggal Lahir", value = "15 Mei 1965")
                }
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "Jenis Kelamin", value = "Perempuan")
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(label = "Kontak", value = "0821321321")
                }
            }

            // Description
            DetailItem(label = "Deskripsi", value = victim.description)

            // Evacuation Status
            Column {
                Text(
                    text = "Status Evakuasi",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DisasterEvacuationStatusBadge(isEvacuated = true)
                    DisasterEvacuationStatusBadge(isEvacuated = false)
                }
            }
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
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

@Composable
private fun PhotoSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Foto Korban",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "Foto Korban (1)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(3) { // Dummy items for photo placeholders
                Card(
                    modifier = Modifier.size(140.dp, 120.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    // Placeholder for an actual image
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.BrokenImage,
                            contentDescription = "Placeholder Image",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                    }
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


@Preview(showBackground = true, name = "Detail Victim Light")
@Composable
fun DisasterVictimDetailScreenLightPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterVictimDetailScreen(navController = rememberNavController(), victimId = "1")
    }
}

@Preview(showBackground = true, name = "Detail Victim Dark")
@Composable
fun DisasterVictimDetailScreenDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterVictimDetailScreen(navController = rememberNavController(), victimId = "1")
    }
}
