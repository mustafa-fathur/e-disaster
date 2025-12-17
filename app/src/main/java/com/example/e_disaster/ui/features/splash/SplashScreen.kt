package com.example.e_disaster.ui.features.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        when (authState) {
            AuthState.AUTHENTICATED -> {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            AuthState.UNAUTHENTICATED -> {
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }

            AuthState.LOADING -> {
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}