package com.example.e_disaster.ui.features.disaster.detail.tabs

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppSearchBar
import com.example.e_disaster.ui.components.FilterChipGroup
import com.example.e_disaster.ui.components.badges.DisasterAidCategoryBadge
import com.example.e_disaster.ui.features.disaster.detail.AidItem
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard
import com.example.e_disaster.ui.features.disaster_aid.DisasterAidUiState
import com.example.e_disaster.ui.features.disaster_aid.DisasterAidViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AidsTabContent(
    navController: NavController,
    disasterId: String,
    viewModel: DisasterAidViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Semua") }

    val categoryMapping = mapOf(
        "Semua" to null,
        "Pangan" to "food",
        "Sandang" to "clothing",
        "Papan" to "housing"
    )

    LaunchedEffect(disasterId, searchQuery, selectedFilter) {
        val category = categoryMapping[selectedFilter]
        
        viewModel.loadDisasterAids(
            disasterId = disasterId,
            search = searchQuery.ifBlank { null },
            category = category
        )
    }

    val filterOptions = listOf("Semua", "Pangan", "Sandang", "Papan")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholderText = "Cari data bantuan..."
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

        when (val state = uiState) {
            is DisasterAidUiState.Loading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is DisasterAidUiState.Success -> {
                val aids = state.data.data ?: emptyList()
                
                if (aids.isEmpty()) {
                    item {
                        Text(
                            text = "Belum ada data bantuan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }
                } else {
                    items(aids, key = { it.id ?: "" }) { aidDto ->
                        val aid = AidItem(
                            id = aidDto.id ?: "",
                            title = aidDto.title ?: "Tidak ada judul",
                            amount = "${aidDto.quantity ?: 0} ${aidDto.unit ?: ""}",
                            description = aidDto.description ?: "",
                            category = aidDto.category ?: ""
                        )
                        
                        ListItemCard(
                            onClick = { navController.navigate("disaster-aid-detail/$disasterId/${aid.id}") }
                        ) {
                            AidCardContent(
                                aid = aid,
                                reporterName = aidDto.reporter?.user?.name,
                                createdAt = aidDto.createdAt
                            )
                        }
                    }
                }
            }
            is DisasterAidUiState.Error -> {
                item {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    )
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun AidCardContent(
    aid: AidItem,
    reporterName: String? = null,
    createdAt: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = aid.title,
            style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
            fontWeight = FontWeight.Bold
        )
        Text(
            text = aid.amount,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = aid.description,
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
                
                val displayText = buildString {
                    append(reporterName ?: "Unknown")
                    if (createdAt != null) {
                        try {
                            val zonedDateTime = ZonedDateTime.parse(createdAt)
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            append(" • ${zonedDateTime.format(formatter)}")
                        } catch (e: Exception) {
                            append(" • $createdAt")
                        }
                    }
                }
                
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box {
                DisasterAidCategoryBadge(category = aid.category)
            }
        }
    }
}