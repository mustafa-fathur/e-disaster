@file:Suppress("DEPRECATION")

package com.example.e_disaster.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.ui.viewmodel.HealthViewModel

@Composable
fun LoginScreen(navController: NavController, healthViewModel: HealthViewModel = viewModel()) {    // These 'remember' states will hold the text from the input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val healthStatus by healthViewModel.healthStatus.collectAsState()

    LaunchedEffect(Unit) {
        healthViewModel.checkHealth()
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            healthStatus?.let {
                Text("Status: ${it.status}")
                Text("Message: ${it.message}")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(
                    id = if (isSystemInDarkTheme()) {
                        R.drawable.dark_app_logo
                    } else {
                        R.drawable.app_logo
                    }
                ),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(128.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "e-Disaster",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 32.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp)) // Adds space between elements

            Text(
                text = "Masuk",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Default
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp)) // Adds space between elements

            // Email Text Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Text Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Email Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                supportingText = {
                    Text(text = "Lupa Password?", color = MaterialTheme.colorScheme.primary)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    /* TODO: Login logic, sekarang direct ke home screen aja langsung... */
                    navController.navigate("home") {
                        popUpTo(0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Masuk")
            }

            Spacer(modifier = Modifier.height(24.dp))

            RegisterLink(navController = navController)
        }
    }
}

@Composable
private fun RegisterLink(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)){
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
