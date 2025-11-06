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
fun DisasterVictimStatusBadge(status: String) {
    val (text, colors) = when (status.lowercase()) {
        "minor_injury" -> "Luka Ringan" to if (isSystemInDarkTheme()) BadgeColors.VictimStatus.Dark.minorInjury else BadgeColors.VictimStatus.Light.minorInjury
        "serious_injuries" -> "Luka Berat" to if (isSystemInDarkTheme()) BadgeColors.VictimStatus.Dark.seriousInjuries else BadgeColors.VictimStatus.Light.seriousInjuries
        "lost" -> "Hilang" to if (isSystemInDarkTheme()) BadgeColors.VictimStatus.Dark.lost else BadgeColors.VictimStatus.Light.lost
        "deceased" -> "Meninggal" to if (isSystemInDarkTheme()) BadgeColors.VictimStatus.Dark.deceased else BadgeColors.VictimStatus.Light.deceased
        else -> "Luka Ringan" to if (isSystemInDarkTheme()) BadgeColors.VictimStatus.Dark.minorInjury else BadgeColors.VictimStatus.Light.minorInjury
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun DisasterVictimStatusBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterVictimStatusBadge(status = "minor_injury")
            DisasterVictimStatusBadge(status = "serious_injuries")
            DisasterVictimStatusBadge(status = "lost")
            DisasterVictimStatusBadge(status = "deceased")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun DisasterVictimStatusBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterVictimStatusBadge(status = "minor_injury")
            DisasterVictimStatusBadge(status = "serious_injuries")
            DisasterVictimStatusBadge(status = "lost")
            DisasterVictimStatusBadge(status = "deceased")
        }
    }
}
