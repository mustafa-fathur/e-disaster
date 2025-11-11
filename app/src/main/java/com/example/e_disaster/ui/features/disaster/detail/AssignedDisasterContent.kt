package com.example.e_disaster.ui.features.disaster.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Identitas", "Laporan", "Korban", "Bantuan")

    val reports = listOf(ReportItem("r1", "Kondisi Terkini", "12 Nov 2025"), ReportItem("r2", "Kerusakan Infrastruktur", "11 Nov 2025"))
    val victims = listOf(VictimItem("v1", "Budi Santoso", "Luka Ringan"), VictimItem("v2", "Siti Aminah", "Hilang"))
    val aids = listOf(AidItem("a1", "Makanan", "100 dus"), AidItem("a2", "Pakaian", "50 karung"))

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
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
