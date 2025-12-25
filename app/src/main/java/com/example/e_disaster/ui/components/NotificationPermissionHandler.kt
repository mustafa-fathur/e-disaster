package com.example.e_disaster.ui.components

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionHandler() {
    // Notification permission is only required on Android 13 (API 33) and above.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS
        )

        if (!notificationPermissionState.status.isGranted) {
            LaunchedEffect(Unit) {
                notificationPermissionState.launchPermissionRequest()
            }
        }
    }
}
    