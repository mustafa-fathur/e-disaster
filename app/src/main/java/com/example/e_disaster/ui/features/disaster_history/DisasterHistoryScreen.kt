package com.example.e_disaster.ui.features.disaster_history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.e_disaster.ui.theme.BadgeColors
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.e_disaster.data.model.History
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.utils.DummyData.historyList
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun HistoryScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel()
) {
    val historyList = historyList
    val user = mainViewModel.user
    // pick success color based on current theme
    val isDarkTheme = isSystemInDarkTheme()
    val successBorderColor = if (isDarkTheme) BadgeColors.DisasterStatus.Dark.completed.border else BadgeColors.DisasterStatus.Light.completed.border
    val successContentColor = if (isDarkTheme) BadgeColors.DisasterStatus.Dark.completed.text else BadgeColors.DisasterStatus.Light.completed.text

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Riwayat",
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
        containerColor = MaterialTheme.colorScheme.surface,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (historyList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Anda belum memiliki riwayat penanganan bencana.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(historyList, key = { it.id }) { history ->
                            // inline card directly instead of calling a separate HistoryCard composable
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .clickable { navController.navigate("disaster-detail/${history.id}") },
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Left column: title, info rows and badges
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = history.disasterName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        InfoRow(icon = Icons.Default.LocationOn, text = history.location)

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row {
                                            OutlinedButton(
                                                onClick = { /* no-op */ },
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, Color(0xFFEF6C3C)),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = Color(0xFFEF6C3C),
                                                    containerColor = Color.Transparent
                                                ),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = history.disasterName.takeIf { it.isNotBlank() } ?: "Jenis",
                                                    fontSize = 12.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            OutlinedButton(
                                                onClick = { /* no-op */ },
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, successBorderColor),
                                                colors = ButtonDefaults.outlinedButtonColors(
                                                    contentColor = successContentColor,
                                                    containerColor = Color.Transparent
                                                ),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = if (history.status.equals("completed", true)) "Selesai" else history.status,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }

                                    // Right image panel (square)
                                    Box(
                                        modifier = Modifier
                                            .width(100.dp)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                            .background(Color(0xFFF1EAF5)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                            model = history.imageUrl,
                                            contentDescription = "Gambar Bencana",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun HistoryListPreview() {
    EDisasterTheme {
        val isDarkTheme = isSystemInDarkTheme()
        val successBorderColor = if (isDarkTheme) BadgeColors.DisasterStatus.Dark.completed.border else BadgeColors.DisasterStatus.Light.completed.border
        val successContentColor = if (isDarkTheme) BadgeColors.DisasterStatus.Dark.completed.text else BadgeColors.DisasterStatus.Light.completed.text
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(listOf(
                History(id = "1", disasterName = "Erupsi Gunung", location = "Lumajang, Jawa Timur", date = "21 Nov 2025", imageUrl = "", status = "completed", description = ""),
                History(id = "2", disasterName = "Banjir Bandang", location = "Garut, Jawa Barat", date = "15 Jul 2024", imageUrl = "", status = "Selesai", description = "")
            )) { history ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp, top = 10.dp, bottom = 10.dp, end = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = history.disasterName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            InfoRow(icon = Icons.Default.LocationOn, text = history.location)

                            Spacer(modifier = Modifier.height(6.dp))

                            Row {
                                OutlinedButton(
                                    onClick = { /* no-op */ },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, Color(0xFFEF6C3C)),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFFEF6C3C),
                                        containerColor = Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = history.disasterName.takeIf { it.isNotBlank() } ?: "Jenis",
                                        fontSize = 12.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                OutlinedButton(
                                    onClick = { /* no-op */ },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, successBorderColor),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = successContentColor,
                                        containerColor = Color.Transparent
                                    ),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (history.status.equals("completed", true)) "Selesai" else history.status,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(100.dp)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                                .background(Color(0xFFF1EAF5)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = history.imageUrl,
                                contentDescription = "Gambar Bencana",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
