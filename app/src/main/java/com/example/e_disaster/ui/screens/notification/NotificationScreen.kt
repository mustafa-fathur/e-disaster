package com.example.e_disaster.ui.screens.notification

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.e_disaster.data.model.Notification
import com.example.e_disaster.data.model.NotificationPriority
import com.example.e_disaster.data.model.NotificationType
import com.example.e_disaster.ui.components.partials.AppBottomNavBar
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.utils.DummyData

@Composable
fun NotificationScreen(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<NotificationType?>(null) }
    val notifications = remember(selectedFilter) {
        selectedFilter?.let { DummyData.getNotificationsByType(it) } ?: DummyData.dummyNotifications
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Notifikasi",
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
            AppBottomNavBar(navController = navController as NavHostController)
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // Filter chips
                NotificationFilterChips(
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it }
                )

                // Notification list
                if (notifications.isEmpty()) {
                    EmptyNotificationState()
                } else {
                    NotificationList(
                        notifications = notifications,
                        onNotificationClick = { notification ->
                            // TODO: Navigate to detail or mark as read
                        }
                    )
                }
            }
        }
    )
}

@Composable
private fun NotificationFilterChips(
    selectedFilter: NotificationType?,
    onFilterChange: (NotificationType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            text = "Semua",
            selected = selectedFilter == null,
            onClick = { onFilterChange(null) }
        )

        FilterChip(
            text = "Bencana",
            selected = selectedFilter == NotificationType.DISASTER,
            onClick = { onFilterChange(NotificationType.DISASTER) }
        )

        FilterChip(
            text = "Bantuan",
            selected = selectedFilter == NotificationType.AID,
            onClick = { onFilterChange(NotificationType.AID) }
        )

        FilterChip(
            text = "Korban",
            selected = selectedFilter == NotificationType.VICTIM,
            onClick = { onFilterChange(NotificationType.VICTIM) }
        )
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = text,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        color = contentColor,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
private fun NotificationList(
    notifications: List<Notification>,
    onNotificationClick: (Notification) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notifications) { notification ->
            NotificationCard(
                notification = notification,
                onClick = { onNotificationClick(notification) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationCard(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Notification icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(getNotificationIconBackground(notification.type)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getNotificationIcon(notification.type),
                    contentDescription = "Notification Icon",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Notification content
            Column(modifier = Modifier.weight(1f)) {
                // Title and priority indicator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (!notification.isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Message
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Time and type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatNotificationTime(notification.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Priority indicator
                        val priorityColor = getPriorityColor(notification.priority)
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(priorityColor)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = getPriorityText(notification.priority),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getNotificationIcon(type: String) = when (type) {
    NotificationType.DISASTER.value -> Icons.Default.Warning
    NotificationType.AID.value -> Icons.Default.Notifications
    NotificationType.VICTIM.value -> Icons.Default.Warning
    else -> Icons.Default.Notifications
}

@Composable
private fun getNotificationIconBackground(type: String) = when (type) {
    NotificationType.DISASTER.value -> Color.Red
    NotificationType.AID.value -> Color.Blue
    NotificationType.VICTIM.value -> Color(0xFFFF9800) // Orange color
    else -> Color.Gray
}

@Composable
private fun getPriorityColor(priority: String) = when (priority) {
    NotificationPriority.LOW.value -> Color.Green
    NotificationPriority.MEDIUM.value -> Color.Yellow
    NotificationPriority.HIGH.value -> Color(0xFFFF9800)
    NotificationPriority.URGENT.value -> Color.Red
    else -> Color.Gray
}

@Composable
private fun getPriorityText(priority: String) = when (priority) {
    NotificationPriority.LOW.value -> "Rendah"
    NotificationPriority.MEDIUM.value -> "Sedang"
    NotificationPriority.HIGH.value -> "Tinggi"
    NotificationPriority.URGENT.value -> "Darurat"
    else -> "Normal"
}

@Composable
private fun formatNotificationTime(createdAt: String): String {
    // Simple time formatting for demo
    return try {
        val date = createdAt.split("T")[0]
        val time = createdAt.split("T")[1].substring(0, 5)
        "$date $time"
    } catch (e: Exception) {
        createdAt
    }
}

@Composable
private fun EmptyNotificationState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "No Notifications",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Belum ada notifikasi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Notifikasi akan muncul ketika ada update bencana atau bantuan",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}