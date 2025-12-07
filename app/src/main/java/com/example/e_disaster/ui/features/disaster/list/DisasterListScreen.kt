package com.example.e_disaster.ui.features.disaster.list

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.ui.components.badges.DisasterStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterTypeBadge
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisasterListScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel = hiltViewModel(),
    disasterListViewModel: DisasterListViewModel = hiltViewModel()
) {
    val user = mainViewModel.user

    var searchQuery by remember { mutableStateOf("") }
    val filterOptions = listOf("Semua", "Gempa Bumi", "Banjir", "BMKG", "Manual")
    var selectedFilter by remember { mutableStateOf(filterOptions.first()) }

    val disasters = disasterListViewModel.disasters
    val isLoading = disasterListViewModel.isLoading
    val errorMessage = disasterListViewModel.errorMessage

    val filteredDisasters = remember(disasters, searchQuery, selectedFilter) {
        disasters.filter { disaster ->
            val matchesSearch = (disaster.title?.contains(searchQuery, ignoreCase = true) ?: false) ||
                    (disaster.description?.contains(searchQuery, ignoreCase = true) ?: false) ||
                    (disaster.location?.contains(searchQuery, ignoreCase = true) ?: false)

            val matchesFilter = when (selectedFilter) {
                "Semua" -> true
                "Gempa Bumi" -> disaster.types.equals("earthquake", ignoreCase = true)
                "Banjir" -> disaster.types.equals("flood", ignoreCase = true)
                "BMKG" -> disaster.source.equals("bmkg", ignoreCase = true)
                "Manual" -> disaster.source.equals("manual", ignoreCase = true)
                else -> true
            }
            matchesSearch && matchesFilter
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Bencana",
                profilePictureUrl = user?.profilePicture,
                canNavigateBack = false,
                onProfileClick = {
                    navController.navigate("profile")
                },
            )
        },
        containerColor = MaterialTheme.colorScheme.surface,
        bottomBar = {
            AppBottomNavBar(navController = navController)
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Cari bencana...") },
                        leadingIcon = { Icon(Icons.Default.Menu, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterOptions) { filter ->
                            OutlinedButton(
                                onClick = { selectedFilter = filter },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (selectedFilter == filter) MaterialTheme.colorScheme.primaryContainer.copy(
                                        alpha = 0.5f
                                    ) else Color.Transparent
                                )
                            ) {
                                Text(
                                    text = filter,
                                    color = if (selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            when {
                isLoading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                errorMessage != null -> {
                    item {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                }
                filteredDisasters.isEmpty() -> {
                    item {
                        Text(
                            text = "Tidak ada data bencana yang ditemukan.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 64.dp)
                        )
                    }
                }
                else -> {
                    items(filteredDisasters, key = { it.id ?: "" }) { disaster ->
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            DisasterListItem(disaster = disaster, navController = navController)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DisasterListItem(disaster: Disaster, navController: NavHostController) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Tanggal tidak valid"
        return try {
            val localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }

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

    Card(
        modifier = Modifier.clickable {
            disaster.id?.let { navController.navigate("disaster-detail/$it") }
        },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_person_24),
                        contentDescription = "Reporter Icon",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dilaporkan oleh ${disaster.source?.replaceFirstChar { it.uppercase() } ?: "Tidak Diketahui"}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Pada ${formatDate(disaster.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AsyncImage(
                model = "https://images.unsplash.com/photo-1543418575-84e1b93302a8?q=80&w=2070&auto=format&fit=crop",
                contentDescription = disaster.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DisasterTypeBadge(type = formatDisasterType(disaster.types))
                    DisasterStatusBadge(status = disaster.status ?: "unknown")
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = disaster.title ?: "Tanpa Judul",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = disaster.description ?: "Tidak ada deskripsi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = "Location",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = disaster.location ?: "Lokasi tidak diketahui",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_date),
                        contentDescription = "Date and Time",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${formatDate(disaster.date)} • ${disaster.time?.take(5) ?: ""} WIB",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
