package com.example.e_disaster.ui.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.utils.DisasterImageProvider

@Composable
fun HomeScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val user = mainViewModel.user
    val stats = homeViewModel.stats
    val recentDisasters = homeViewModel.recentDisasters
    val isLoading = homeViewModel.isLoading
    val errorMessage = homeViewModel.errorMessage

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Beranda",
                profilePictureUrl = user?.profilePicture,
                canNavigateBack = false,
                onProfileClick = {
                    navController.navigate("profile")
                },
            )
        },
        bottomBar = {
            AppBottomNavBar(navController = navController as NavHostController)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dashboard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        isLoading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                        errorMessage != null -> {
                            Text(
                                text = errorMessage,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        else -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                DashboardCard(
                                    title = "Total Bencana Terjadi",
                                    count = stats?.totalDisasters ?: 0,
                                    icon = Icons.Default.Warning,
                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                    iconColor = MaterialTheme.colorScheme.error
                                )
                                DashboardCard(
                                    title = "Total Bencana Berlangsung",
                                    count = stats?.ongoingDisasters ?: 0,
                                    icon = Icons.Default.Warning,
                                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                    iconColor = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                DashboardCard(
                                    title = "Total Bencana Ditangani",
                                    count = stats?.assignedDisasters ?: 0,
                                    icon = Icons.Default.Warning,
                                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    iconColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                DashboardCard(
                                    title = "Total Bencana Diselesaikan",
                                    count = stats?.completedDisasters ?: 0,
                                    icon = Icons.Default.CheckCircle,
                                    backgroundColor = Color(0xFFE8F5E9),
                                    iconColor = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Bencana Aktif",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { navController.navigate("disaster-list") }) {
                            Text(
                                text = "Lihat Semua >",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (recentDisasters.isEmpty()) {
                    Text(
                        text = "Tidak ada bencana aktif",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        items(recentDisasters) { disaster ->
                            DisasterItem(disaster = disaster)
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardCard(
    title: String,
    count: Int,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun DisasterItem(disaster: DisasterDto) {
    fun formatDisasterType(type: String?): String {
        return when (type?.lowercase()) {
            "earthquake" -> "Gempa Bumi"
            "tsunami" -> "Tsunami"
            "volcanic_eruption" -> "Gunung Meletus"
            "flood" -> "Banjir"
            "drought" -> "Kekeringan"
            "tornado" -> "Angin Topan"
            "landslide" -> "Tanah Longsor"
            "non_natural_disaster" -> "Bencana Non Alam"
            "social_disaster" -> "Bencana Sosial"
            else -> "Lainnya"
        }
    }

    fun formatStatus(status: String?): String {
        return when (status?.lowercase()) {
            "ongoing" -> "Berlangsung"
            "completed" -> "Selesai"
            "cancelled" -> "Dibatalkan"
            else -> "Tidak Diketahui"
        }
    }

    Card(
        modifier = Modifier
            .width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box {
                val imageUrl = disaster.pictures?.firstOrNull()?.url
                AsyncImage(
                    model = DisasterImageProvider.getImageUrlFromString(imageUrl ?: ""),
                    contentDescription = disaster.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.Gray), // Fallback background
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = formatStatus(disaster.status),
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = formatDisasterType(disaster.types),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = disaster.title ?: "Tanpa Judul",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = disaster.location ?: "Lokasi tidak diketahui",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_date),
                        contentDescription = "Date",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${disaster.date ?: ""} • ${disaster.time?.take(5) ?: ""} WIB",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    EDisasterTheme {
        HomeScreen(navController = navController)
    }
}
