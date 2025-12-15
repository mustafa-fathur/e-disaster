package com.example.e_disaster.ui.features.disaster.detail.tabs

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.ui.components.AppSearchBar
import com.example.e_disaster.ui.components.FilterChipGroup
import com.example.e_disaster.ui.components.badges.DisasterEvacuationStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterVictimStatusBadge
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun VictimsTabContent(
    navController: NavController,
    disasterId: String,
    victims: List<DisasterVictim>
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredVictims = remember(searchQuery, selectedFilter, victims) {
        victims.filter { victim ->
            val matchesSearch = victim.name.contains(searchQuery, ignoreCase = true) ||
                    victim.nik.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Semua" -> true
                "Luka Ringan" -> victim.status.equals("minor_injury", ignoreCase = true)
                "Luka Berat" -> victim.status.equals("serious_injuries", ignoreCase = true)
                "Meninggal" -> victim.status.equals("deceased", ignoreCase = true)
                "Hilang" -> victim.status.equals("missing", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val filterOptions = listOf("Semua", "Luka Ringan", "Luka Berat", "Meninggal", "Hilang")

    Column {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholderText = "Cari nama atau NIK..."
            )
            FilterChipGroup(
                filterOptions = filterOptions,
                selectedFilter = selectedFilter,
                onFilterSelected = { newFilter ->
                    selectedFilter = newFilter
                }
            )
        }

        if (filteredVictims.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Tidak ada data korban.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredVictims) { victim ->
                    ListItemCard(
                        onClick = {
                            if (victim.id.isEmpty() || victim.disasterId.isNullOrEmpty()) {
                                Log.e(
                                    "VictimsTab",
                                    "Navigasi dibatalkan: ID kosong. VictimID: '${victim.id}', DisasterID: '${victim.disasterId}'"
                                )
                            } else {
                                navController.navigate("disaster-victim-detail/${victim.disasterId}/${victim.id}")
                            }
                        }
                    ) {
                        VictimCardContent(victim = victim)
                    }
                }
            }
        }
    }
}

@Composable
private fun VictimCardContent(victim: DisasterVictim) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        fun formatDate(dateString: String?): String {
            if (dateString.isNullOrEmpty()) return "Tanggal tidak valid"
            return try {
                val localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            } catch (e: Exception) {
                "Tanggal tidak valid"
            }
        }
        Text(
            text = victim.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${victim.gender} • ${victim.contactInfo}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = victim.description,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Reporter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = victim.reporterName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = formatDate(victim.createdAt),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterEvacuationStatusBadge(isEvacuated = victim.isEvacuated)
            DisasterVictimStatusBadge(status = victim.status)
        }
    }
}
