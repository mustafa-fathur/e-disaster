package com.example.e_disaster.ui.screens.disaster

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.AppTopAppBar

@Composable
fun DisasterDetailScreen(navController: NavController, disasterId: String?) {
    val reportId = "coba-coba-le-2026"
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate("update-disaster/$disasterId")
                        }, colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ubah")
                    }
                }
            )
        },
        bottomBar = {
            DisasterDetailBottomBar(
                onVictimClick = {
                    navController.navigate("disaster-victim-list/$disasterId")
                },
                onMapClick = { /* TODO: Navigate to map view */ },
                onAidClick = {
                    navController.navigate("disaster-aid-list/$disasterId")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ini halaman detail bencana dengan ID bencana: $disasterId.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("add-disaster-report/$disasterId")
            }) {
                Text("Tambah Laporan Perkembangan Bencana")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                navController.navigate("update-disaster-report/$reportId")
            }) {
                Text("Ubah Laporan Perkembangan Bencana")
            }
        }
    }
}

@Composable
private fun DisasterDetailBottomBar(
    onVictimClick: () -> Unit,
    onMapClick: () -> Unit,
    onAidClick: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DetailBottomNavItem(
                label = "Daftar Korban",
                icon = Icons.Default.Person,
                onClick = onVictimClick
            )

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onMapClick),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Lihat Peta"
                        )
                    }
                }
            }

            DetailBottomNavItem(
                label = "Daftar Bantuan",
                icon = Icons.Default.Favorite,
                onClick = onAidClick
            )
        }
    }
}

@Composable
private fun RowScope.DetailBottomNavItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .height(56.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray
        )
    }
}

