package com.example.e_disaster.ui.components.badges

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.example.e_disaster.ui.theme.BadgeColors

@Composable
fun DisasterTypeBadge(type: String) {
    val colorSet = when (type.lowercase()) {
        "gempa bumi" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.earthquake else BadgeColors.DisasterType.Light.earthquake
        "tsunami" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.tsunami else BadgeColors.DisasterType.Light.tsunami
        "gunung meletus" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.volcanicEruption else BadgeColors.DisasterType.Light.volcanicEruption
        "banjir" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.flood else BadgeColors.DisasterType.Light.flood
        "kekeringan" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.drought else BadgeColors.DisasterType.Light.drought
        "angin topan" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.tornado else BadgeColors.DisasterType.Light.tornado
        "tanah longsor" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.landslide else BadgeColors.DisasterType.Light.landslide
        "bencana non alam" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.nonNaturalDisaster else BadgeColors.DisasterType.Light.nonNaturalDisaster
        "bencana sosial" -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.socialDisaster else BadgeColors.DisasterType.Light.socialDisaster
        else -> if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.earthquake else BadgeColors.DisasterType.Light.earthquake
    }
    BaseBadge(text = type, colorSet = colorSet)
}
