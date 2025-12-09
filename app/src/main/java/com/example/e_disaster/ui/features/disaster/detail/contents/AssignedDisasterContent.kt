package com.example.e_disaster.ui.features.disaster.detail.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.ui.features.disaster.detail.AidItem
import com.example.e_disaster.ui.features.disaster.detail.DisasterReportsViewModel
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

    val reportsViewModel: DisasterReportsViewModel = hiltViewModel()
    val reportsUiState = reportsViewModel.uiState

    val dummyAids = remember {
        listOf(
            AidItem("1", "Beras 1 Ton", "1000 Kg", "Bantuan dari gudang pusat.", "food"),
            AidItem("2", "Selimut", "500 Pcs", "Selimut tebal untuk pengungsi.", "clothing")
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = initialTabIndex,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = initialTabIndex == index,
                    onClick = { onTabSelected(index) },
                    text = { Text(title) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        when (initialTabIndex) {
            0 -> IdentityTabContent(disaster = disaster)

            1 -> {
                when {
                    reportsUiState.isLoading -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator()
                        }
                    }

                    reportsUiState.errorMessage != null -> {
                        ReportsTabContent(navController, emptyList())
                    }

                    else -> {
                        ReportsTabContent(navController, reportsUiState.reports)
                    }
                }
            }

            2 -> VictimsTabContent(
                navController = navController,
                disasterId = disaster.id ?: "",
                victims = victims
            )

            3 -> AidsTabContent(navController, dummyAids)
        }
    }
}
