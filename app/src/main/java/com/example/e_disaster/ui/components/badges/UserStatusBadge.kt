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
fun UserStatusBadge(status: String) {
    val (text, colors) = when (status.lowercase()) {
        "registered" -> "Mendaftar" to if (isSystemInDarkTheme()) BadgeColors.UsersStatus.Dark.registered else BadgeColors.UsersStatus.Light.registered
        "active" -> "Aktif" to if (isSystemInDarkTheme()) BadgeColors.UsersStatus.Dark.active else BadgeColors.UsersStatus.Light.active
        "inactive" -> "Tidak Aktif" to if (isSystemInDarkTheme()) BadgeColors.UsersStatus.Dark.inactive else BadgeColors.UsersStatus.Light.inactive
        else -> "Mendaftar" to if (isSystemInDarkTheme()) BadgeColors.UsersStatus.Dark.registered else BadgeColors.UsersStatus.Light.registered
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun UserStatusBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UserStatusBadge(status = "registered")
            UserStatusBadge(status = "active")
            UserStatusBadge(status = "inactive")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun UserStatusBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UserStatusBadge(status = "registered")
            UserStatusBadge(status = "active")
            UserStatusBadge(status = "inactive")
        }
    }
}
