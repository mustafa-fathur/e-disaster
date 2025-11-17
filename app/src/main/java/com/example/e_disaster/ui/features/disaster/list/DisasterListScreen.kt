package com.example.e_disaster.ui.features.disaster.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.ui.components.badges.DisasterStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterTypeBadge
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import com.example.e_disaster.ui.theme.EDisasterTheme

data class DisasterListItemData(
    val id: String,
    val reporterName: String,
    val reporterImageUrl: String? = null,
    val reportDate: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val location: String,
    val dateTime: String,
    val type: String,
    val status: String,
    val magnitude: Double? = null,
    val depth: Int? = null
)

val dummyDisasterList = listOf(
    DisasterListItemData(
        id = "1",
        reporterName = "Mustafa Fathur Rahman",
        reportDate = "11 November 2025",
        title = "Gempa Bumi Cianjur",
        description = "Gempa bumi berkekuatan 5.6 SR mengguncang wilayah Cianjur dan sekitarnya",
        imageUrl = "https://images.unsplash.com/photo-1534224039824-c7a01e09b154?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        location = "Cianjur, Jawa Barat",
        dateTime = "2024-10-28 • 13:21 WIB",
        type = "Gempa Bumi",
        status = "ongoing",
        magnitude = 5.6,
        depth = 10
    ),
    DisasterListItemData(
        id = "2",
        reporterName = "Mustafa Fathur Rahman",
        reportDate = "11 November 2025",
        title = "Banjir Bandang Jakarta Selatan",
        description = "Banjir bandang melanda kawasan Jakarta Selatan akibat hujan deras",
        imageUrl = "https://images.unsplash.com/photo-1567697879034-b5a26d4b358b?q=80&w=1932&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        location = "Cianjur, Jawa Barat",
        dateTime = "2024-10-28 • 13:21 WIB",
        type = "Banjir",
        status = "completed",
    )
)

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

    val realDisasters = disasterListViewModel.realDisasters
    val isLoading = disasterListViewModel.isLoading
    val errorMessage = disasterListViewModel.errorMessage

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(16.dp))
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
                                border = BorderStroke(1.dp, if(selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if(selectedFilter == filter) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color.Transparent
                                )
                            ) {
                                Text(text = filter, color = if(selectedFilter == filter) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            items(dummyDisasterList, key = { it.id }) {
                Column(Modifier.padding(horizontal = 16.dp)){
                    DisasterListItem(disaster = it, navController = navController)
                }
            }

            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = "Prototype Data Bencana Asli dari API",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(realDisasters, key = { it.id }) { disaster ->
                    RealDisasterItemPrototype(disaster = disaster, navController = navController)
                }
            }

            // Add some space at the bottom
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun DisasterListItem(disaster: DisasterListItemData, navController: NavHostController) {
    Card(
        modifier = Modifier.clickable { navController.navigate("disaster-detail/${disaster.id}") },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column() {
            // Reporter Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (disaster.reporterImageUrl != null) {
                    AsyncImage(
                        model = disaster.reporterImageUrl,
                        contentDescription = "Reporter",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = disaster.reporterName.first().toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = disaster.reporterName, fontWeight = FontWeight.Bold)
                    Text(text = disaster.reportDate, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            // Image
            AsyncImage(
                model = disaster.imageUrl,
                contentDescription = disaster.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            // Details
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = disaster.title, style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = disaster.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_location), contentDescription = "Location", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = disaster.location, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_date), contentDescription = "Date", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = disaster.dateTime, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DisasterTypeBadge(type = disaster.type)
                    DisasterStatusBadge(status = disaster.status)
                }
                if (disaster.magnitude != null && disaster.depth != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Magnitudo ${disaster.magnitude} SR • Kedalaman: ${disaster.depth} km", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun RealDisasterItemPrototype(disaster: Disaster, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable {
                // Navigate to the detail screen with the real disaster ID
                navController.navigate("disaster-detail/${disaster.id}")
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = disaster.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(text = disaster.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = disaster.description, style = MaterialTheme.typography.bodyMedium, maxLines = 2)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DisasterListScreenPreview() {
    val navController = rememberNavController()
    EDisasterTheme {
        DisasterListScreen(navController = navController)
    }
}
