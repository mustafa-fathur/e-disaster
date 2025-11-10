package com.example.e_disaster.ui.features.disaster_aid

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.e_disaster.data.model.AidStatus
import com.example.e_disaster.data.model.AidType
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster_aid.DisasterAidViewModel
import com.example.e_disaster.utils.Resource

@Composable
fun NearbyAidsScreen(
    navController: NavController,
    userLatitude: Double? = null,
    userLongitude: Double? = null,
    viewModel: DisasterAidViewModel = viewModel()
) {
    val nearbyAids by viewModel.nearbyAids.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val radius by viewModel.radius.collectAsState()

    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Load data saat screen pertama kali dibuka
    LaunchedEffect(userLatitude, userLongitude) {
        if (userLatitude != null && userLongitude != null) {
            currentLocation = Pair(userLatitude, userLongitude)
            viewModel.loadNearbyAids(userLatitude, userLongitude)
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Bantuan Terdekat",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = {
                        currentLocation?.let { (lat, lng) ->
                            viewModel.refreshData(latitude = lat, longitude = lng)
                        }
                    }) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                    IconButton(onClick = { /* TODO: Show location picker */ }) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Pilih Lokasi"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Location info
            currentLocation?.let { (lat, lng) ->
                LocationInfo(
                    latitude = lat,
                    longitude = lng,
                    radius = radius
                )
            }

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
            when (nearbyAids) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Resource.Success -> {
                    val aids = (nearbyAids as Resource.Success<List<DisasterAid>>).data
                    if (aids.isNullOrEmpty()) {
                        EmptyStateNearby()
                    } else {
                        NearbyAidList(aids = aids)
                    }
                }
                is Resource.Error -> {
                    ErrorState(
                        message = (nearbyAids as Resource.Error<List<DisasterAid>>).message ?: "Terjadi kesalahan",
                        onRetry = {
                            currentLocation?.let { (lat, lng) ->
                                viewModel.loadNearbyAids(lat, lng)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationInfo(
    latitude: Double,
    longitude: Double,
    radius: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "Lokasi",
                tint = MaterialTheme.colorScheme.primary
            )
            // Spacer removed for compatibility
            Column {
                Text(
                    text = "Lokasi Anda",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Radius: ${radius}km",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
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
private fun NearbyAidList(aids: List<DisasterAid>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(aids) { aid ->
            NearbyAidCard(aid = aid)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NearbyAidCard(aid: DisasterAid) {
    Card(
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

                    if (aid.disasterName != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Bencana: ${aid.disasterName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (aid.location != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Lokasi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            // Spacer removed for compatibility
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

                    Spacer(modifier = Modifier.height(4.dp))

                    if (aid.distance != null) {
                        Text(
                            text = "${String.format("%.1f", aid.distance)} km",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
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
private fun EmptyStateNearby() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tidak ada bantuan di sekitar lokasi Anda",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Coba perbesar radius pencarian atau periksa lokasi Anda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
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
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}
