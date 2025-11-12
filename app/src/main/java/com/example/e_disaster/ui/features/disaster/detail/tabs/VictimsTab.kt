package com.example.e_disaster.ui.features.disaster.detail.tabs

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppSearchBar
import com.example.e_disaster.ui.components.FilterChipGroup
import com.example.e_disaster.ui.components.badges.DisasterEvacuationStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterVictimStatusBadge
import com.example.e_disaster.ui.features.disaster.VictimItem
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard

@Composable
fun VictimsTabContent(navController: NavController, victims: List<VictimItem>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val filteredVictims = remember(searchQuery, selectedFilter, victims) {
        victims.filter { victim ->
            val matchesSearch = victim.name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = when (selectedFilter) {
                "Semua" -> true
                "Luka Ringan" -> victim.status.equals("minor_injury", ignoreCase = true)
                "Luka Berat" -> victim.status.equals("serious_injuries", ignoreCase = true)
                "Hilang" -> victim.status.equals("lost", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    val filterOptions = listOf("Semua", "Luka Ringan", "Luka Berat", "Hilang")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholderText = "Cari nama korban..."
                )
                FilterChipGroup(
                    filterOptions = filterOptions,
                    selectedFilter = selectedFilter,
                    onFilterSelected = { newFilter ->
                        selectedFilter = newFilter
                    }
                )
            }
        }

        items(filteredVictims) { victim ->
            ListItemCard(
                onClick = { navController.navigate("disaster-victim-detail/${victim.id}") }
            ) {
                VictimCardContent(victim = victim)
            }
        }
    }
}

@Composable
private fun VictimCardContent(victim: VictimItem) {
    // This composable defines the layout inside each victim card
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Top row: Name
        Text(
            text = victim.name,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
            fontWeight = FontWeight.Bold
        )

        // Middle row: Gender and Phone
        Text(
            text = "Laki-laki • 08226810", // Placeholder data
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Description row
        Text(
            text = victim.description, // Assuming VictimItem has a description field
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Footer row: Reporter and Timestamp
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
                    text = "Ahmad Wijaya", // Placeholder reporter name
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "2024-10-28 15:30", // Placeholder timestamp
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Badges row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterEvacuationStatusBadge(isEvacuated = victim.isEvacuated)
            DisasterVictimStatusBadge(status = victim.status)
        }
    }
}
