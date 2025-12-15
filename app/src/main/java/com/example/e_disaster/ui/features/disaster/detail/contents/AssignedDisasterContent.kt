package com.example.e_disaster.ui.features.disaster.detail.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.ui.features.disaster.detail.AidItem
import com.example.e_disaster.ui.features.disaster.detail.ReportItem
import com.example.e_disaster.ui.features.disaster.detail.tabs.AidsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.IdentityTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.ReportsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.VictimsTabContent

@Composable
fun AssignedDisasterContent(
    navController: NavController,
    disaster: Disaster,
    victims: List<DisasterVictim>,
    initialTabIndex: Int,
    onTabSelected: (Int) -> Unit
) {

    val tabs = listOf("Identitas", "Laporan", "Korban", "Bantuan")

    val dummyReports = remember {
        listOf(
            ReportItem("1", "Laporan Awal", "2024-10-28"),
            ReportItem("2", "Laporan Perkembangan", "2024-10-29")
        )
    }
    val dummyAids = remember {
        listOf(
            AidItem("1", "Beras 1 Ton", "1000 Kg", "Bantuan dari gudang pusat.", "food"),
            AidItem("2", "Selimut", "500 Pcs", "Selimut tebal untuk pengungsi.", "clothing")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(selectedTabIndex = initialTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = initialTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) }
                )
            }
        }

        disaster.id?.let { disasterId ->
            when (initialTabIndex) {
                0 -> IdentityTabContent(disaster = disaster)
                1 -> ReportsTabContent(navController, dummyReports)
                2 -> VictimsTabContent(
                    navController = navController,
                    disasterId = disasterId,
                    victims = victims
                )

                3 -> AidsTabContent(navController, dummyAids)
            }
        }
    }
}
