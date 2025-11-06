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
fun UserTypeBadge(type: String) {
    val (text, colors) = when (type.lowercase()) {
        "admin" -> "Admin" to if (isSystemInDarkTheme()) BadgeColors.UsersType.Dark.admin else BadgeColors.UsersType.Light.admin
        "officer" -> "Petugas" to if (isSystemInDarkTheme()) BadgeColors.UsersType.Dark.officer else BadgeColors.UsersType.Light.officer
        "volunteer" -> "Sukarelawan" to if (isSystemInDarkTheme()) BadgeColors.UsersType.Dark.volunteer else BadgeColors.UsersType.Light.volunteer
        else -> "Sukarelawan" to if (isSystemInDarkTheme()) BadgeColors.UsersType.Dark.volunteer else BadgeColors.UsersType.Light.volunteer
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun UserTypeBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UserTypeBadge(type = "admin")
            UserTypeBadge(type = "officer")
            UserTypeBadge(type = "volunteer")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun UserTypeBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            UserTypeBadge(type = "admin")
            UserTypeBadge(type = "officer")
            UserTypeBadge(type = "volunteer")
        }
    }
}
