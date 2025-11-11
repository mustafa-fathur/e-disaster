package com.example.e_disaster.ui.components.badges

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.e_disaster.ui.theme.BadgeColors

@Composable
fun DisasterEvacuationStatusBadge(isEvacuated: Boolean) {
    val (text, colors) = if (isEvacuated) {
        "Sudah Dievakuasi" to if (isSystemInDarkTheme()) {
            BadgeColors.VictimStatus.Dark.evacuated
        } else {
            BadgeColors.VictimStatus.Light.evacuated
        }
    } else {
        "Belum Dievakuasi" to if (isSystemInDarkTheme()) {
            BadgeColors.VictimStatus.Dark.notEvacuated
        } else {
            BadgeColors.VictimStatus.Light.notEvacuated
        }
    }
    BaseBadge(text = text, colorSet = colors)
}
