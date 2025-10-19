package com.example.e_disaster.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.ui.components.AppBottomNavBar
import com.example.e_disaster.ui.components.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme

data class Disaster(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val imageUrl: String
)

@Composable
fun DisasterListScreen(navController: NavHostController) {

    val dummyDisasters = listOf(
        Disaster(
            "1",
            "Banjir Padang",
            "Ketinggian air mencapai 1 meter di daerah Ulak Karang.",
            "Sedang Berlangsung",
            "https://images.unsplash.com/photo-1657069345471-c54f2432b79c?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=870"
        ),
        Disaster(
            "2",
            "Kebakaran Bukittinggi",
            "Kebakaran hebat terjadi di Pasar Atas.",
            "Selesai",
            "https://images.unsplash.com/photo-1495467033336-2effd8753d51?ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&q=80&w=870"
        )
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Daftar Bencana",
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavBar(navController = navController)
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "Daftar Bencana Terkini",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(dummyDisasters) { disaster ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            AsyncImage(
                                model = disaster.imageUrl,
                                contentDescription = disaster.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(disaster.title, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(disaster.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                disaster.status,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
@Preview(showBackground = true)
fun DisasterListScreenPreview() {
    val navController = rememberNavController()
    EDisasterTheme {
        DisasterListScreen(navController = navController)
    }
}
