package com.example.e_disaster.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.AppTopAppBar


@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Profil",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ProfileHeader(
                name = "Mustafa Fathur Rahman",
                email = "fathur@edisaster.com"
            )

            Spacer(modifier = Modifier.height(32.dp))

            ProfileMenuItem(text = "Update Profile", onClick = { /*TODO*/ })
            ProfileMenuItem(text = "Update Password", onClick = { /*TODO*/ })
            ProfileMenuItem(text = "Bantuan", onClick = { /*TODO*/ })
            ProfileMenuItem(text = "Tentang Aplikasi", onClick = { /*TODO*/ })
            ProfileMenuItem(text = "Logout", onClick = { navController.navigate("login") })

            Spacer(modifier = Modifier.weight(1f))

            FooterCredit()
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit, isLogout: Boolean = false) {
    Column {
        Text(
            text = text,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            color = if (isLogout) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        HorizontalDivider(
            Modifier,
            DividerDefaults.Thickness,
            color = Color.LightGray.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun FooterCredit() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.codenity),
            contentDescription = "Codenity Studio Logo",
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = "Made by Codenity Studio",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
