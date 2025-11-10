@file:Suppress("DEPRECATION")

package com.example.e_disaster.ui.features.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel() // Use Hilt to get the ViewModel
) {
    // Collect the UI state from the ViewModel
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // This effect runs when the uiState changes to Success or Error
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                // Navigate to home and clear the back stack so the user can't go back to login
                navController.navigate("home") {
                    popUpTo(0)
                }
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }
            else -> {} // Do nothing for Idle or Loading states
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = viewModel.healthCheckMessage,
                color = if (viewModel.healthCheckMessage.contains("API Error")) Color.Red else Color.Gray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = painterResource(
                    id = if (isSystemInDarkTheme()) R.drawable.dark_app_logo else R.drawable.app_logo
                ),
                contentDescription = "Logo",
                modifier = Modifier.size(128.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "e-Disaster",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Masuk",
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Default),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Text Field - Now connected to the ViewModel
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                isError = uiState is LoginUiState.Error // Highlight field on error
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Text Field - Now connected to the ViewModel
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                isError = uiState is LoginUiState.Error // Highlight field on error
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button - Now connected to the ViewModel
            Button(
                onClick = { viewModel.loginUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = uiState !is LoginUiState.Loading // Disable button while loading
            ) {
                if (uiState is LoginUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Masuk")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            RegisterLink(navController = navController)
        }
    }
}

// RegisterLink and Previews remain the same
@Composable
private fun RegisterLink(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
            append("Belum punya akun? ")
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            pushStringAnnotation(tag = "REGISTER", annotation = "register")
            append("Daftar Relawan")
            pop()
        }
    }

    ClickableText(
        text = annotatedText,
        onClick = { offset ->
            annotatedText.getStringAnnotations(tag = "REGISTER", start = offset, end = offset)
                .firstOrNull()?.let {
                    navController.navigate("register")
                }
        }
    )
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun LoginScreenLight() {
    EDisasterTheme(darkTheme = false) {
        LoginScreen(navController = NavController(LocalContext.current))
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun LoginScreenDark() {
    EDisasterTheme(darkTheme = true) {
        LoginScreen(navController = NavController(LocalContext.current))
    }
}