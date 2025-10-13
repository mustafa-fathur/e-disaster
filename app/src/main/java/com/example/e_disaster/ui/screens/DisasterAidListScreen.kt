package com.example.e_disaster.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.e_disaster.data.model.AidStatus
import com.example.e_disaster.data.model.AidType
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.ui.components.AppTopAppBar
import com.example.e_disaster.ui.viewmodel.DisasterAidViewModel
import com.example.e_disaster.utils.DummyData
import com.example.e_disaster.utils.Resource

@Composable
fun DisasterAidListScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: DisasterAidViewModel = viewModel()
) {
    val disasterAids by viewModel.disasterAids.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val radius by viewModel.radius.collectAsState()

    var filteredAids by remember { mutableStateOf<List<DisasterAid>>(emptyList()) }

    // Filter data berdasarkan selected filters
    LaunchedEffect(disasterAids, selectedStatus, selectedType) {
        val aids = when (disasterAids) {
            is Resource.Success -> (disasterAids as Resource.Success<List<DisasterAid>>).data ?: emptyList()
            else -> emptyList()
        }

        filteredAids = aids.filter { aid ->
            (selectedStatus == null || aid.status == selectedStatus?.value) &&
            (selectedType == null || aid.type == selectedType?.value)
        }
    }

    // Load data saat screen pertama kali dibuka (untuk demo menggunakan data dummy)
    LaunchedEffect(disasterId) {
        // Untuk sementara gunakan data dummy, nanti diganti dengan API call
        val dummyAids = DummyData.dummyDisasterAids
        viewModel.loadDisasterAids("demo-disaster-id") // Placeholder untuk demo
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Bantuan",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = {
                        disasterId?.let { viewModel.refreshData(it) }
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = { /* TODO: Show filter dialog */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Filter"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("add-disaster-aid/$disasterId")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Bantuan")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Filter chips
            FilterChips(
                selectedStatus = selectedStatus,
                selectedType = selectedType,
                radius = radius,
                onStatusChange = { viewModel.setStatusFilter(it) },
                onTypeChange = { viewModel.setTypeFilter(it) },
                onRadiusChange = { viewModel.setRadius(it) },
                onResetFilters = { viewModel.resetFilters() }
            )

            // Content
            when (disasterAids) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    if (filteredAids.isEmpty()) {
                        if (selectedStatus != null || selectedType != null) {
                            EmptyFilterState(
                                onResetFilters = { viewModel.resetFilters() }
                            )
                        } else {
                            EmptyState(
                                onAddAid = { navController.navigate("add-disaster-aid/$disasterId") }
                            )
                        }
                    } else {
                        AidList(
                            aids = filteredAids,
                            onAidClick = { aid ->
                                navController.navigate("update-disaster-aid/${aid.id}")
                            }
                        )
                    }
                }
                is Resource.Error -> {
                    ErrorState(
                        message = (disasterAids as Resource.Error<List<DisasterAid>>).message ?: "Terjadi kesalahan",
                        onRetry = {
                            disasterId?.let { viewModel.loadDisasterAids(it) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChips(
    selectedStatus: AidStatus?,
    selectedType: AidType?,
    radius: Double,
    onStatusChange: (AidStatus?) -> Unit,
    onTypeChange: (AidType?) -> Unit,
    onRadiusChange: (Double) -> Unit,
    onResetFilters: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedStatus != null) {
            OutlinedButton(
                onClick = { onStatusChange(null) },
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = selectedStatus.value,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (selectedType != null) {
            OutlinedButton(
                onClick = { onTypeChange(null) },
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = selectedType.value,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (radius != 10.0) {
            OutlinedButton(
                onClick = { onRadiusChange(10.0) },
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "${radius}km",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (selectedStatus != null || selectedType != null || radius != 10.0) {
            OutlinedButton(
                onClick = onResetFilters,
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Reset",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AidList(
    aids: List<DisasterAid>,
    onAidClick: (DisasterAid) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(aids) { aid ->
            AidCard(
                aid = aid,
                onClick = { onAidClick(aid) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AidCard(
    aid: DisasterAid,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = aid.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${aid.quantity} ${aid.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (aid.location != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = aid.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    StatusChip(status = aid.status)

                    if (aid.distance != null) {
                        Text(
                            text = "${String.format("%.1f", aid.distance)} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (aid.description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = aid.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (aid.contactPerson != null) {
                    Text(
                        text = "Kontak: ${aid.contactPerson}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (aid.estimatedArrival != null) {
                    Text(
                        text = "ETA: ${aid.estimatedArrival}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(status: String) {
    val (color, textColor) = when (status) {
        "tersedia" -> Color.Green to Color.White
        "habis" -> Color.Red to Color.White
        "dalam_perjalanan" -> Color.Yellow to Color.Black
        "terdistribusi" -> Color.Gray to Color.White
        else -> Color.Gray to Color.White
    }

    Text(
        text = status.replace("_", " ").replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.bodySmall,
        color = textColor,
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp),
        fontSize = 10.sp
    )
}

@Composable
private fun EmptyState(onAddAid: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Belum ada bantuan yang tersedia",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onAddAid) {
            Text("Tambah Bantuan Pertama")
        }
    }
}

@Composable
private fun EmptyFilterState(onResetFilters: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tidak ada bantuan yang sesuai dengan filter",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onResetFilters) {
            Text("Reset Filter")
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Terjadi Kesalahan",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}