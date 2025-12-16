package com.example.e_disaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.e_disaster.ui.components.NotificationPermissionHandler
import com.example.e_disaster.ui.navigation.NavGraph
import com.example.e_disaster.ui.theme.EDisasterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val initialIntent = intent

        setContent {
            NotificationPermissionHandler()
            EDisasterTheme {
                // Surface provides a background color and handles drawing elevation
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(intent = initialIntent)
                }
            }
        }
    }
}

