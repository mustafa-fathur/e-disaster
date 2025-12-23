package com.example.e_disaster.ui.features.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.e_disaster.R
import com.example.e_disaster.data.remote.dto.notification.NotificationDto
import com.example.e_disaster.data.remote.dto.notification.NotificationStatsDto
import com.example.e_disaster.ui.components.AppSearchBar
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.components.partials.MainViewModel
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun NotificationListScreen(
    navController: NavController,
    mainViewModel: MainViewModel = hiltViewModel(),
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val showUnreadOnly by viewModel.showUnreadOnly.collectAsState()
    
    val user = mainViewModel.user

    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var notificationToDelete by remember { mutableStateOf<NotificationDto?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Notifikasi",
                profilePictureUrl = user?.profilePicture,
                canNavigateBack = false,
                onProfileClick = { navController.navigate("profile") },
                actions = {
                    // Mark all as read button
                    if ((stats?.unread ?: 0) > 0) {
                        TextButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Tandai semua dibaca",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tandai Semua")
                        }
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavBar(navController = navController as NavHostController)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Stats header
            NotificationStatsHeader(
                stats = stats,
                onDeleteAllRead = { showDeleteAllDialog = true }
            )

            // Category filter chips
            CategoryFilterChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { viewModel.setCategory(it) },
                stats = stats
            )

            // Search bar
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                AppSearchBar(
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        viewModel.setSearchQuery(it.ifBlank { null })
                    },
                    placeholderText = "Cari notifikasi..."
                )
            }

            // Notification list
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                uiState.errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "Error",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                uiState.notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "No notifications",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Tidak ada notifikasi",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.notifications, key = { it.id ?: "" }) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    // Mark as read
                                    if (notification.isRead == false) {
                                        notification.id?.let { viewModel.markAsRead(it) }
                                    }
                                    // Navigate to related screen
                                    navigateFromNotification(navController, notification)
                                },
                                onDelete = { notificationToDelete = notification }
                            )
                        }
                    }
                }
            }
        }

        // Delete confirmation dialog
        if (notificationToDelete != null) {
            AlertDialog(
                onDismissRequest = { notificationToDelete = null },
                title = { Text("Hapus Notifikasi") },
                text = { Text("Apakah Anda yakin ingin menghapus notifikasi ini?") },
                confirmButton = {
                    Button(
                        onClick = {
                            notificationToDelete?.id?.let { viewModel.deleteNotification(it) }
                            notificationToDelete = null
                        }
                    ) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { notificationToDelete = null }) {
                        Text("Batal")
                    }
                }
            )
        }

        // Delete all read dialog
        if (showDeleteAllDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text("Hapus Semua yang Sudah Dibaca") },
                text = { Text("Apakah Anda yakin ingin menghapus semua notifikasi yang sudah dibaca?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteAllRead()
                            showDeleteAllDialog = false
                        }
                    ) {
                        Text("Hapus Semua")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteAllDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Composable
private fun NotificationStatsHeader(
    stats: NotificationStatsDto?,
    onDeleteAllRead: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Notifikasi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${stats?.total ?: 0}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatItem(
                    label = "Belum Dibaca",
                    value = stats?.unread ?: 0,
                    color = MaterialTheme.colorScheme.error
                )
                StatItem(
                    label = "Sudah Dibaca",
                    value = stats?.read ?: 0,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            if ((stats?.read ?: 0) > 0) {
                IconButton(onClick = onDeleteAllRead) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus semua yang sudah dibaca",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun CategoryFilterChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    stats: NotificationStatsDto?
) {
    val categories = listOf(
        null to "Semua",
        "new_disaster" to "Bencana",
        "new_disaster_report" to "Laporan",
        "new_disaster_victim_report" to "Korban",
        "new_disaster_aid_report" to "Bantuan",
        "disaster_status_changed" to "Status",
        "volunteer_verification" to "Verifikasi"
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (key, label) ->
            val count = if (key == null) {
                stats?.total
            } else {
                stats?.byCategory?.get(key)
            }

            FilterChip(
                selected = selectedCategory == key,
                onClick = { onCategorySelected(key) },
                label = {
                    Text(
                        text = if (count != null && count > 0) {
                            "$label ($count)"
                        } else {
                            label
                        }
                    )
                },
                leadingIcon = if (selectedCategory == key) {
                    { Icon(getCategoryIcon(key), contentDescription = null, modifier = Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationDto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead == true)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead == true) 0.dp else 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Category icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getCategoryColor(notification.category).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(notification.category),
                    contentDescription = notification.category,
                    tint = getCategoryColor(notification.category),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Unread indicator + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (notification.isRead == false) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    Text(
                        text = notification.title ?: "Notifikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (notification.isRead == false) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Message
                Text(
                    text = notification.message ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Category badge + time
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Badge(
                        containerColor = getCategoryColor(notification.category).copy(alpha = 0.2f),
                        contentColor = getCategoryColor(notification.category)
                    ) {
                        Text(
                            text = getCategoryLabel(notification.category),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Text(
                        text = formatTimeAgo(notification.sentAt ?: notification.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// Get icon based on category
fun getCategoryIcon(category: String?): ImageVector {
    return when (category) {
        "new_disaster" -> Icons.Default.Warning
        "new_disaster_report" -> Icons.Default.Description
        "new_disaster_victim_report" -> Icons.Default.LocalHospital
        "new_disaster_aid_report" -> Icons.Default.Campaign  // Package/box icon
        "disaster_status_changed" -> Icons.Default.CheckCircle
        "volunteer_verification" -> Icons.Default.PersonAdd
        else -> Icons.Default.Notifications
    }
}

// Get color based on category
@Composable
fun getCategoryColor(category: String?): Color {
    return when (category) {
        "new_disaster" -> MaterialTheme.colorScheme.error
        "new_disaster_report" -> MaterialTheme.colorScheme.primary
        "new_disaster_victim_report" -> Color(0xFFE91E63)  // Pink for victims
        "new_disaster_aid_report" -> Color(0xFF4CAF50)     // Green for aids
        "disaster_status_changed" -> MaterialTheme.colorScheme.tertiary
        "volunteer_verification" -> Color(0xFF9C27B0)       // Purple for verification
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}

// Get label in Indonesian
fun getCategoryLabel(category: String?): String {
    return when (category) {
        "new_disaster" -> "Bencana Baru"
        "new_disaster_report" -> "Laporan Baru"
        "new_disaster_victim_report" -> "Korban Baru"
        "new_disaster_aid_report" -> "Bantuan Baru"
        "disaster_status_changed" -> "Status Berubah"
        "volunteer_verification" -> "Verifikasi"
        else -> "Lainnya"
    }
}

// Format time ago
fun formatTimeAgo(dateString: String?): String {
    if (dateString == null) return ""
    
    return try {
        val dateTime = ZonedDateTime.parse(dateString)
        val now = ZonedDateTime.now()
        val duration = Duration.between(dateTime, now)

        when {
            duration.toDays() > 0 -> "${duration.toDays()}h yang lalu"
            duration.toHours() > 0 -> "${duration.toHours()}j yang lalu"
            duration.toMinutes() > 0 -> "${duration.toMinutes()}m yang lalu"
            else -> "Baru saja"
        }
    } catch (e: Exception) {
        dateString
    }
}

// Navigate based on notification category
fun navigateFromNotification(navController: NavController, notification: NotificationDto) {
    val data = notification.data
    
    when (notification.category) {
        "new_disaster", "disaster_status_changed" -> {
            data?.disasterId?.let { disasterId ->
                navController.navigate("disaster-detail/$disasterId")
            }
        }
        "new_disaster_report" -> {
            data?.disasterId?.let { disasterId ->
                data.reportId?.let { reportId ->
                    navController.navigate("disaster-report-detail/$disasterId/$reportId")
                }
            }
        }
        "new_disaster_victim_report" -> {
            data?.disasterId?.let { disasterId ->
                data.victimId?.let { victimId ->
                    navController.navigate("disaster-victim-detail/$disasterId/$victimId")
                }
            }
        }
        "new_disaster_aid_report" -> {
            data?.disasterId?.let { disasterId ->
                data.aidId?.let { aidId ->
                    navController.navigate("disaster-aid-detail/$disasterId/$aidId")
                }
            }
        }
        else -> {
            // Default: just mark as read, no navigation
        }
    }
}

