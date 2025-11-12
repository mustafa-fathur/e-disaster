package com.example.e_disaster.ui.features.disaster.detail.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.features.disaster.AidItem
import com.example.e_disaster.ui.features.disaster.detail.components.ListItemCard

@Composable
fun AidsTabContent(navController: NavController, aids: List<AidItem>) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(aids) { aid ->
            // Call the updated ListItemCard
            ListItemCard(
                onClick = { navController.navigate("disaster-victim-detail/${aid.id}") }
            ) {
                // This is the custom content for the card's body.
                // It can be as simple or complex as you need.
                Text(
                    text = aid.amount,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                // LATER, you could add more things here:
                // e.g. UserStatusBadge(status = victim.status)
            }
        }
    }
}
