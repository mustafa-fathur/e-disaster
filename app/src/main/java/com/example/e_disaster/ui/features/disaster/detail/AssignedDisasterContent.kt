package com.example.e_disaster.ui.features.disaster.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow // <-- IMPORT a non-deprecated TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf // <-- Use mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.e_disaster.ui.features.disaster.AidItem
import com.example.e_disaster.ui.features.disaster.ReportItem
import com.example.e_disaster.ui.features.disaster.VictimItem
import com.example.e_disaster.ui.features.disaster.detail.tabs.AidsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.IdentityTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.ReportsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.VictimsTabContent

@Composable
fun AssignedDisasterContent(navController: NavController, disasterId: String?) {
    // Use mutableIntStateOf for primitive types like Int for better performance
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Identitas", "Laporan", "Korban", "Bantuan")

    // The disasterId is passed down for potential use in fetching data for these tabs
    // In a real app, you'd pass this disasterId to a ViewModel.
    val reports = listOf(ReportItem("r1", "Kondisi Terkini", "12 Nov 2025"), ReportItem("r2", "Kerusakan Infrastruktur", "11 Nov 2025"))
    val victims = listOf(
        VictimItem("v1", "Budi Santoso", "Luka ringan di kaki akibat reruntuhan", "minor_injury", true),
        VictimItem("v2", "Siti Aminah", "Belum ditemukan sejak kejadian bencana", "lost", false)
    )
    val aids = listOf(AidItem("a1", "Bantuan Makanan", "100 dus", "Bantuan Makanan dari pemerintah", "food"), AidItem("a2", "Pakaian Bagus", "50 karung", "Pakaian Layak dari Hamba Allah", "clothes"))

    Column(modifier = Modifier.fillMaxSize()) {
        // Use PrimaryTabRow instead of the deprecated TabRow
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        when (selectedTabIndex) {
            0 -> IdentityTabContent()
            1 -> ReportsTabContent(navController, reports)
            2 -> VictimsTabContent(navController, victims)
            3 -> AidsTabContent(navController, aids)
        }
    }
}
