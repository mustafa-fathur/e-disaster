package com.example.e_disaster.ui.features.disaster.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun ListItemCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    // The entire content is now a flexible slot that provides a ColumnScope
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline)
    ) {
        // Pass the content lambda to a Column
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

// Updated preview for clarity
@Preview(showBackground = true, name = "List Item Card")
@Composable
fun ListItemCardPreview() {
    EDisasterTheme{
        ListItemCard(
            onClick = {}
        ) {
            // Example of custom content for the body
            Text(
                text = "Card Title",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "This is the custom content area.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
