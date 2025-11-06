package com.example.e_disaster.ui.components.badges

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_disaster.ui.theme.BadgeColors
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun DisasterStatusBadge(status: String) {
    val (text, colors) = when (status.lowercase()) {
        "cancelled" -> "Dibatalkan" to if (isSystemInDarkTheme()) BadgeColors.DisasterStatus.Dark.cancelled else BadgeColors.DisasterStatus.Light.cancelled
        "ongoing" -> "Berlangsung" to if (isSystemInDarkTheme()) BadgeColors.DisasterStatus.Dark.ongoing else BadgeColors.DisasterStatus.Light.ongoing
        "completed" -> "Selesai" to if (isSystemInDarkTheme()) BadgeColors.DisasterStatus.Dark.completed else BadgeColors.DisasterStatus.Light.completed
        else -> "Berlangsung" to if (isSystemInDarkTheme()) BadgeColors.DisasterStatus.Dark.ongoing else BadgeColors.DisasterStatus.Light.ongoing
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun DisasterStatusBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterStatusBadge(status = "cancelled")
            DisasterStatusBadge(status = "ongoing")
            DisasterStatusBadge(status = "completed")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun DisasterStatusBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterStatusBadge(status = "cancelled")
            DisasterStatusBadge(status = "ongoing")
            DisasterStatusBadge(status = "completed")
        }
    }
}
