package com.example.e_disaster.ui.features.disaster.detail.contents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.ui.features.disaster.detail.AidItem
import com.example.e_disaster.ui.features.disaster.detail.DisasterReportsViewModel
import com.example.e_disaster.ui.features.disaster.detail.VictimItem
import com.example.e_disaster.ui.features.disaster.detail.tabs.AidsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.IdentityTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.ReportsTabContent
import com.example.e_disaster.ui.features.disaster.detail.tabs.VictimsTabContent

@Composable
fun AssignedDisasterContent(
    navController: NavController,
    disaster: Disaster
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Identitas", "Laporan", "Korban", "Bantuan")

    // obtain reports view model scoped to the same NavBackStackEntry so SavedStateHandle contains "disasterId"
    val reportsViewModel: DisasterReportsViewModel = hiltViewModel()
    val reportsUiState = reportsViewModel.uiState

    val victims = listOf(
        VictimItem(
            "v1",
            "Budi Santoso",
            "Luka ringan di kaki akibat reruntuhan",
            "minor_injury",
            true
        ),
        VictimItem("v2", "Siti Aminah", "Belum ditemukan sejak kejadian bencana", "lost", false)
    )
    val aids = listOf(
        AidItem(
            "a1",
            "Bantuan Makanan",
            "100 dus",
            "Bantuan Makanan dari pemerintah",
            "food"
        ), AidItem("a2", "Pakaian Bagus", "50 karung", "Pakaian Layak dari Hamba Allah", "clothes")
    )

    Column(modifier = Modifier.fillMaxSize()) {
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
            0 -> IdentityTabContent(disaster = disaster)
            1 -> {
                // Pass actual reports from ViewModel to the Reports tab
                when {
                    reportsUiState.isLoading -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator(modifier = Modifier)
                        }
                    }
                    reportsUiState.errorMessage != null -> {
                        // show empty list and could show an error UI; keep simple for now
                        ReportsTabContent(navController, emptyList())
                    }
                    else -> {
                        ReportsTabContent(navController, reportsUiState.reports)
                    }
                }
            }
            2 -> VictimsTabContent(navController, victims)
            3 -> AidsTabContent(navController, aids)
        }
    }
}
