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
fun DisasterTypeBadge(type: String) {
    val (text, colors) = when (type.lowercase()) {
        "earthquake" -> "Gempa Bumi" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.earthquake else BadgeColors.DisasterType.Light.earthquake
        "tsunami" -> "Tsunami" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.tsunami else BadgeColors.DisasterType.Light.tsunami
        "volcanic_eruption" -> "Gunung Meletus" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.volcanicEruption else BadgeColors.DisasterType.Light.volcanicEruption
        "flood" -> "Banjir" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.flood else BadgeColors.DisasterType.Light.flood
        "drought" -> "Kekeringan" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.drought else BadgeColors.DisasterType.Light.drought
        "tornado" -> "Angin Topan" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.tornado else BadgeColors.DisasterType.Light.tornado
        "landslide" -> "Tanah Longsor" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.landslide else BadgeColors.DisasterType.Light.landslide
        "non_natural_disaster" -> "Bencana Non Alam" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.nonNaturalDisaster else BadgeColors.DisasterType.Light.nonNaturalDisaster
        "social_disaster" -> "Bencana Sosial" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.socialDisaster else BadgeColors.DisasterType.Light.socialDisaster
        else -> "Gempa Bumi" to if (isSystemInDarkTheme()) BadgeColors.DisasterType.Dark.earthquake else BadgeColors.DisasterType.Light.earthquake
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun DisasterTypeBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterTypeBadge(type = "earthquake")
            DisasterTypeBadge(type = "tsunami")
            DisasterTypeBadge(type = "volcanic_eruption")
            DisasterTypeBadge(type = "flood")
            DisasterTypeBadge(type = "drought")
            DisasterTypeBadge(type = "tornado")
            DisasterTypeBadge(type = "landslide")
            DisasterTypeBadge(type = "non_natural_disaster")
            DisasterTypeBadge(type = "social_disaster")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun DisasterTypeBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterTypeBadge(type = "earthquake")
            DisasterTypeBadge(type = "tsunami")
            DisasterTypeBadge(type = "volcanic_eruption")
            DisasterTypeBadge(type = "flood")
            DisasterTypeBadge(type = "drought")
            DisasterTypeBadge(type = "tornado")
            DisasterTypeBadge(type = "landslide")
            DisasterTypeBadge(type = "non_natural_disaster")
            DisasterTypeBadge(type = "social_disaster")
        }
    }
}
