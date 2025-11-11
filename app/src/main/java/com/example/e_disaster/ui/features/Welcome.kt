package com.example.e_disaster.ui.features

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun Welcome() {
    Surface {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(
                        id = if (isSystemInDarkTheme()) {
                            R.drawable.dark_app_logo
                        } else {
                            R.drawable.app_logo
                        }
                    ),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(100.dp, 100.dp),
                )
                Text(
                    text = "e-Disaster",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
fun WelcomePreviewLight() {
    EDisasterTheme(darkTheme = false) {
        Welcome()
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
fun WelcomePreviewDark() {
    EDisasterTheme(darkTheme = true) {
        Welcome()
    }
}
