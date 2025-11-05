// BadgeColors.kt - File baru khusus untuk badge colors
package com.example.e_disaster.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Badge color configuration untuk E-Disaster app
 * Setiap badge punya 3 komponen: background, text, border
 */
data class BadgeColorSet(
    val background: Color,
    val text: Color,
    val border: Color
)

/**
 * Object singleton yang menyimpan semua badge colors
 * Organized by category untuk kemudahan maintenance
 */
object BadgeColors {

    // ==================== USERS TYPE ====================
    object UsersType {
        object Light {
            val admin = BadgeColorSet(
                background = Color(0xFFE3F2FD),
                text = Color(0xFF0D47A1),
                border = Color(0xFF2196F3)
            )
            val officer = BadgeColorSet(
                background = Color(0xFFF3E5F5),
                text = Color(0xFF4A148C),
                border = Color(0xFF9C27B0)
            )
            val volunteer = BadgeColorSet(
                background = Color(0xFFE8F5E9),
                text = Color(0xFF1B5E20),
                border = Color(0xFF4CAF50)
            )
        }

        object Dark {
            val admin = BadgeColorSet(
                background = Color(0xFF1E3A5F),
                text = Color(0xFF90CAF9),
                border = Color(0xFF42A5F5)
            )
            val officer = BadgeColorSet(
                background = Color(0xFF4A1E5F),
                text = Color(0xFFCE93D8),
                border = Color(0xFFAB47BC)
            )
            val volunteer = BadgeColorSet(
                background = Color(0xFF1E4620),
                text = Color(0xFF81C784),
                border = Color(0xFF66BB6A)
            )
        }
    }

    // ==================== USERS STATUS ====================
    object UsersStatus {
        object Light {
            val registered = BadgeColorSet(
                background = Color(0xFFFFF3E0),
                text = Color(0xFFE65100),
                border = Color(0xFFFB8C00)
            )
            val active = BadgeColorSet(
                background = Color(0xFFE8F5E9),
                text = Color(0xFF1B5E20),
                border = Color(0xFF4CAF50)
            )
            val inactive = BadgeColorSet(
                background = Color(0xFFECEFF1),
                text = Color(0xFF37474F),
                border = Color(0xFF78909C)
            )
        }

        object Dark {
            val registered = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFB74D),
                border = Color(0xFFFFA726)
            )
            val active = BadgeColorSet(
                background = Color(0xFF1E4620),
                text = Color(0xFF81C784),
                border = Color(0xFF66BB6A)
            )
            val inactive = BadgeColorSet(
                background = Color(0xFF2C3338),
                text = Color(0xFFB0BEC5),
                border = Color(0xFF90A4AE)
            )
        }
    }

    // ==================== DISASTER TYPE ====================
    object DisasterType {
        object Light {
            val earthquake = BadgeColorSet(
                background = Color(0xFFEFEBE9),
                text = Color(0xFF3E2723),
                border = Color(0xFF6D4C41)
            )
            val tsunami = BadgeColorSet(
                background = Color(0xFFE1F5FE),
                text = Color(0xFF01579B),
                border = Color(0xFF0288D1)
            )
            val volcanicEruption = BadgeColorSet(
                background = Color(0xFFFBE9E7),
                text = Color(0xFFBF360C),
                border = Color(0xFFFF5722)
            )
            val flood = BadgeColorSet(
                background = Color(0xFFE0F7FA),
                text = Color(0xFF006064),
                border = Color(0xFF00ACC1)
            )
            val drought = BadgeColorSet(
                background = Color(0xFFFFF8E1),
                text = Color(0xFFF57F17),
                border = Color(0xFFFBC02D)
            )
            val tornado = BadgeColorSet(
                background = Color(0xFFF1F8E9),
                text = Color(0xFF33691E),
                border = Color(0xFF689F38)
            )
            val landslide = BadgeColorSet(
                background = Color(0xFFEFEBE9),
                text = Color(0xFF4E342E),
                border = Color(0xFF795548)
            )
            val nonNaturalDisaster = BadgeColorSet(
                background = Color(0xFFFCE4EC),
                text = Color(0xFF880E4F),
                border = Color(0xFFC2185B)
            )
            val socialDisaster = BadgeColorSet(
                background = Color(0xFFF3E5F5),
                text = Color(0xFF4A148C),
                border = Color(0xFF7B1FA2)
            )
        }

        object Dark {
            val earthquake = BadgeColorSet(
                background = Color(0xFF3E3230),
                text = Color(0xFFBCAAA4),
                border = Color(0xFFA1887F)
            )
            val tsunami = BadgeColorSet(
                background = Color(0xFF1E3D5F),
                text = Color(0xFF81D4FA),
                border = Color(0xFF4FC3F7)
            )
            val volcanicEruption = BadgeColorSet(
                background = Color(0xFF5F2E1E),
                text = Color(0xFFFFAB91),
                border = Color(0xFFFF8A65)
            )
            val flood = BadgeColorSet(
                background = Color(0xFF1E4A4F),
                text = Color(0xFF80DEEA),
                border = Color(0xFF4DD0E1)
            )
            val drought = BadgeColorSet(
                background = Color(0xFF5F5320),
                text = Color(0xFFFFF59D),
                border = Color(0xFFFFEE58)
            )
            val tornado = BadgeColorSet(
                background = Color(0xFF2E4620),
                text = Color(0xFFAED581),
                border = Color(0xFF9CCC65)
            )
            val landslide = BadgeColorSet(
                background = Color(0xFF3E3230),
                text = Color(0xFFBCAAA4),
                border = Color(0xFFA1887F)
            )
            val nonNaturalDisaster = BadgeColorSet(
                background = Color(0xFF4F1E3A),
                text = Color(0xFFF48FB1),
                border = Color(0xFFEC407A)
            )
            val socialDisaster = BadgeColorSet(
                background = Color(0xFF3E1E4F),
                text = Color(0xFFCE93D8),
                border = Color(0xFFAB47BC)
            )
        }
    }

    // ==================== DISASTER STATUS ====================
    object DisasterStatus {
        object Light {
            val cancelled = BadgeColorSet(
                background = Color(0xFFECEFF1),
                text = Color(0xFF455A64),
                border = Color(0xFF78909C)
            )
            val ongoing = BadgeColorSet(
                background = Color(0xFFFFEBEE),
                text = Color(0xFFC62828),
                border = Color(0xFFE53935)
            )
            val completed = BadgeColorSet(
                background = Color(0xFFE8F5E9),
                text = Color(0xFF2E7D32),
                border = Color(0xFF4CAF50)
            )
        }

        object Dark {
            val cancelled = BadgeColorSet(
                background = Color(0xFF2C3338),
                text = Color(0xFFB0BEC5),
                border = Color(0xFF90A4AE)
            )
            val ongoing = BadgeColorSet(
                background = Color(0xFF5F1E1E),
                text = Color(0xFFEF9A9A),
                border = Color(0xFFEF5350)
            )
            val completed = BadgeColorSet(
                background = Color(0xFF1E4620),
                text = Color(0xFF81C784),
                border = Color(0xFF66BB6A)
            )
        }
    }

    // ==================== DISASTER SOURCE ====================
    object DisasterSource {
        object Light {
            val bmkg = BadgeColorSet(
                background = Color(0xFFE3F2FD),
                text = Color(0xFF1565C0),
                border = Color(0xFF2196F3)
            )
            val manual = BadgeColorSet(
                background = Color(0xFFFFF3E0),
                text = Color(0xFFEF6C00),
                border = Color(0xFFFB8C00)
            )
        }

        object Dark {
            val bmkg = BadgeColorSet(
                background = Color(0xFF1E3A5F),
                text = Color(0xFF90CAF9),
                border = Color(0xFF42A5F5)
            )
            val manual = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFB74D),
                border = Color(0xFFFFA726)
            )
        }
    }

    // ==================== DISASTER VICTIM STATUS ====================
    object VictimStatus {
        object Light {
            val minorInjury = BadgeColorSet(
                background = Color(0xFFFFF9C4),
                text = Color(0xFFF57F17),
                border = Color(0xFFFBC02D)
            )
            val seriousInjuries = BadgeColorSet(
                background = Color(0xFFFFE0B2),
                text = Color(0xFFE65100),
                border = Color(0xFFFB8C00)
            )
            val lost = BadgeColorSet(
                background = Color(0xFFF3E5F5),
                text = Color(0xFF6A1B9A),
                border = Color(0xFF8E24AA)
            )
            val deceased = BadgeColorSet(
                background = Color(0xFFFFEBEE),
                text = Color(0xFFB71C1C),
                border = Color(0xFFD32F2F)
            )
        }

        object Dark {
            val minorInjury = BadgeColorSet(
                background = Color(0xFF5F5320),
                text = Color(0xFFFFF59D),
                border = Color(0xFFFFEE58)
            )
            val seriousInjuries = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFCC80),
                border = Color(0xFFFFB74D)
            )
            val lost = BadgeColorSet(
                background = Color(0xFF4A1E5F),
                text = Color(0xFFCE93D8),
                border = Color(0xFFBA68C8)
            )
            val deceased = BadgeColorSet(
                background = Color(0xFF5F1E1E),
                text = Color(0xFFEF9A9A),
                border = Color(0xFFE57373)
            )
        }
    }

    // ==================== DISASTER AID CATEGORY ====================
    object AidCategory {
        object Light {
            val food = BadgeColorSet(
                background = Color(0xFFFFF3E0),
                text = Color(0xFFE65100),
                border = Color(0xFFFB8C00)
            )
            val clothing = BadgeColorSet(
                background = Color(0xFFE1F5FE),
                text = Color(0xFF01579B),
                border = Color(0xFF0288D1)
            )
            val housing = BadgeColorSet(
                background = Color(0xFFF1F8E9),
                text = Color(0xFF33691E),
                border = Color(0xFF689F38)
            )
            val medicine = BadgeColorSet(
                background = Color(0xFFFCE4EC),
                text = Color(0xFFAD1457),
                border = Color(0xFFD81B60)
            )
        }

        object Dark {
            val food = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFB74D),
                border = Color(0xFFFFA726)
            )
            val clothing = BadgeColorSet(
                background = Color(0xFF1E3D5F),
                text = Color(0xFF81D4FA),
                border = Color(0xFF4FC3F7)
            )
            val housing = BadgeColorSet(
                background = Color(0xFF2E4620),
                text = Color(0xFFAED581),
                border = Color(0xFF9CCC65)
            )
            val medicine = BadgeColorSet(
                background = Color(0xFF4F1E3A),
                text = Color(0xFFF48FB1),
                border = Color(0xFFF06292)
            )
        }
    }

    // ==================== PICTURE TYPE ====================
    object PictureType {
        object Light {
            val profile = BadgeColorSet(
                background = Color(0xFFE3F2FD),
                text = Color(0xFF0D47A1),
                border = Color(0xFF2196F3)
            )
            val disaster = BadgeColorSet(
                background = Color(0xFFFFEBEE),
                text = Color(0xFFB71C1C),
                border = Color(0xFFE53935)
            )
            val report = BadgeColorSet(
                background = Color(0xFFFFF3E0),
                text = Color(0xFFE65100),
                border = Color(0xFFFB8C00)
            )
            val victim = BadgeColorSet(
                background = Color(0xFFF3E5F5),
                text = Color(0xFF4A148C),
                border = Color(0xFF9C27B0)
            )
            val aid = BadgeColorSet(
                background = Color(0xFFE0F2F1),
                text = Color(0xFF004D40),
                border = Color(0xFF00897B)
            )
        }

        object Dark {
            val profile = BadgeColorSet(
                background = Color(0xFF1E3A5F),
                text = Color(0xFF90CAF9),
                border = Color(0xFF42A5F5)
            )
            val disaster = BadgeColorSet(
                background = Color(0xFF5F1E1E),
                text = Color(0xFFEF9A9A),
                border = Color(0xFFEF5350)
            )
            val report = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFB74D),
                border = Color(0xFFFFA726)
            )
            val victim = BadgeColorSet(
                background = Color(0xFF4A1E5F),
                text = Color(0xFFCE93D8),
                border = Color(0xFFAB47BC)
            )
            val aid = BadgeColorSet(
                background = Color(0xFF1E4D47),
                text = Color(0xFF80CBC4),
                border = Color(0xFF4DB6AC)
            )
        }
    }

    // ==================== NOTIFICATION TYPE ====================
    object NotificationType {
        object Light {
            val volunteerVerification = BadgeColorSet(
                background = Color(0xFFE8F5E9),
                text = Color(0xFF2E7D32),
                border = Color(0xFF4CAF50)
            )
            val newDisaster = BadgeColorSet(
                background = Color(0xFFFFEBEE),
                text = Color(0xFFC62828),
                border = Color(0xFFE53935)
            )
            val newDisasterReport = BadgeColorSet(
                background = Color(0xFFFFF3E0),
                text = Color(0xFFEF6C00),
                border = Color(0xFFFB8C00)
            )
            val newDisasterVictimReport = BadgeColorSet(
                background = Color(0xFFF3E5F5),
                text = Color(0xFF6A1B9A),
                border = Color(0xFF8E24AA)
            )
            val newDisasterAidReport = BadgeColorSet(
                background = Color(0xFFE0F2F1),
                text = Color(0xFF00695C),
                border = Color(0xFF00897B)
            )
            val disasterStatusChanged = BadgeColorSet(
                background = Color(0xFFE3F2FD),
                text = Color(0xFF1565C0),
                border = Color(0xFF2196F3)
            )
        }

        object Dark {
            val volunteerVerification = BadgeColorSet(
                background = Color(0xFF1E4620),
                text = Color(0xFF81C784),
                border = Color(0xFF66BB6A)
            )
            val newDisaster = BadgeColorSet(
                background = Color(0xFF5F1E1E),
                text = Color(0xFFEF9A9A),
                border = Color(0xFFEF5350)
            )
            val newDisasterReport = BadgeColorSet(
                background = Color(0xFF5F3E1E),
                text = Color(0xFFFFB74D),
                border = Color(0xFFFFA726)
            )
            val newDisasterVictimReport = BadgeColorSet(
                background = Color(0xFF4A1E5F),
                text = Color(0xFFCE93D8),
                border = Color(0xFFBA68C8)
            )
            val newDisasterAidReport = BadgeColorSet(
                background = Color(0xFF1E4D47),
                text = Color(0xFF80CBC4),
                border = Color(0xFF4DB6AC)
            )
            val disasterStatusChanged = BadgeColorSet(
                background = Color(0xFF1E3A5F),
                text = Color(0xFF90CAF9),
                border = Color(0xFF42A5F5)
            )
        }
    }
}