package com.example.e_disaster.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun AppBottomNavBar(
    onFabClick: () -> Unit
) {
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface,
        actions = {
            BottomNavItem(
                icon = Icons.Default.Home,
                label = "Beranda",
                isSelected = true
            )

            BottomNavItem(
                icon = Icons.AutoMirrored.Filled.List,
                label = "Daftar Bencana"
            )

            Spacer(modifier = Modifier.weight(1f))

            BottomNavItem(
                icon = Icons.Default.Refresh,
                label = "Riwayat"
            )

            BottomNavItem(
                icon = Icons.Default.Notifications,
                label = "Notifikasi"
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Bencana",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )
}
@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Text(
            text = label,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

@Preview
@Composable
fun BottomNavBarPreview() {
    EDisasterTheme {
        AppBottomNavBar(onFabClick = {})
    }
}