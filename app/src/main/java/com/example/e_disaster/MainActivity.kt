package com.example.e_disaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.data.local.UserPreferences
import com.example.e_disaster.data.remote.UnauthorizedHandler
import com.example.e_disaster.ui.components.NotificationPermissionHandler
import com.example.e_disaster.ui.navigation.NavGraph
import com.example.e_disaster.ui.theme.EDisasterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var unauthorizedHandler: UnauthorizedHandler

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var navController: NavHostController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        setContent {
            navController = rememberNavController()

            LaunchedEffect(Unit) {
                unauthorizedHandler.onUnauthorized.onEach {
                    userPreferences.clearAll()
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }.launchIn(this)
            }

            NotificationPermissionHandler()
            EDisasterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        navController = navController,
                        intent = intent
                    )
                }
            }
        }
    }
}

