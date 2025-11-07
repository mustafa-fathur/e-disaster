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
fun DisasterAidCategoryBadge(category: String) {
    val (text, colors) = when (category.lowercase()) {
        "food" -> "Makanan" to if (isSystemInDarkTheme()) BadgeColors.AidCategory.Dark.food else BadgeColors.AidCategory.Light.food
        "clothing" -> "Pakaian" to if (isSystemInDarkTheme()) BadgeColors.AidCategory.Dark.clothing else BadgeColors.AidCategory.Light.clothing
        "housing" -> "Fasilitas" to if (isSystemInDarkTheme()) BadgeColors.AidCategory.Dark.housing else BadgeColors.AidCategory.Light.housing
        "medicine" -> "Obat-obatan" to if (isSystemInDarkTheme()) BadgeColors.AidCategory.Dark.medicine else BadgeColors.AidCategory.Light.medicine
        else -> "Makanan" to if (isSystemInDarkTheme()) BadgeColors.AidCategory.Dark.food else BadgeColors.AidCategory.Light.food
    }
    BaseBadge(text = text, colorSet = colors)
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun DisasterAidCategoryBadgePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterAidCategoryBadge(category = "food")
            DisasterAidCategoryBadge(category = "clothing")
            DisasterAidCategoryBadge(category = "housing")
            DisasterAidCategoryBadge(category = "medicine")
        }
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun DisasterAidCategoryBadgePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DisasterAidCategoryBadge(category = "food")
            DisasterAidCategoryBadge(category = "clothing")
            DisasterAidCategoryBadge(category = "housing")
            DisasterAidCategoryBadge(category = "medicine")
        }
    }
}
