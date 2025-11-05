@file:Suppress("DEPRECATION")

package com.example.e_disaster.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.e_disaster.ui.viewmodel.HealthViewModel
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun LoginScreen(navController: NavController, healthViewModel: HealthViewModel = viewModel()) {    // These 'remember' states will hold the text from the input fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val healthStatus by healthViewModel.healthStatus.collectAsState()

    LaunchedEffect(Unit) {
        healthViewModel.checkHealth()
    }

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
            painter = painterResource(id = R.drawable.app_logo),
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

        Spacer(modifier = Modifier.height(24.dp)) // Adds space between elements

        // Email Text Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email atau Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Text Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
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

@Composable
private fun RegisterLink(navController: NavController) {
    val annotatedText = buildAnnotatedString {
        append("Sukarelawan? ")
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
            pushStringAnnotation(tag = "REGISTER", annotation = "register")
            append("Daftar")
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

@Preview
@Composable
private fun LoginScreenPreview() {
    EDisasterTheme {
        LoginScreen(
            navController = NavController(LocalContext.current),
        )
    }
}