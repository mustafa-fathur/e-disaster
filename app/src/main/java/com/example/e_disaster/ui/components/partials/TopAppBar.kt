package com.example.e_disaster.ui.components.partials

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    profilePictureUrl: String? = null,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            ) },
        navigationIcon = {
            if (canNavigateBack) {
                // Show Back Arrow
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                // Show App Logo
                IconButton(onClick = { /* Logo is not clickable for now */ }) {
                    Image(
                        painter = painterResource(
                            id = if (isSystemInDarkTheme()) {
                                R.drawable.dark_app_logo
                            } else {
                                R.drawable.app_logo
                            }
                        ),
                        contentDescription = "App Logo"
                    )
                }
            }
        },
        actions = {
            if (actions != null) {
                actions()
            } else {
                if (!canNavigateBack) {
                    // Show Profile Icon only on main screens
                    IconButton(onClick = onProfileClick) {
                        Card(
                            shape = CircleShape,
                            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                            modifier = Modifier.size(32.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            AsyncImage(
                                model = profilePictureUrl,
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(id = R.drawable.codenity),
                                error = painterResource(id = R.drawable.codenity),
                                modifier = Modifier.clip(CircleShape)
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

// Preview (Contoh)
@Preview(name = "Main TopAppBar", showBackground = true)
@Composable
fun MainTopAppBarPreview() {
    EDisasterTheme {
        AppTopAppBar(
            title = "e-Disaster",
            profilePictureUrl = null,
            canNavigateBack = false,
            onProfileClick = {}
        )
    }
}

@Preview(name = "Detail TopAppBar", showBackground = true)
@Composable
fun DetailTopAppBarPreview() {
    EDisasterTheme {
        AppTopAppBar(
            title = "Detail Bencana",
            canNavigateBack = true,
            onNavigateUp = {},
            onProfileClick = {}
        )
    }
}

@Preview(name = "Detail with Edit Action", showBackground = true)
@Composable
fun DetailWithEditActionPreview() {
    EDisasterTheme {
        AppTopAppBar(
            title = "Detail Bencana",
            canNavigateBack = true,
            onProfileClick = {},
            onNavigateUp = {},
            actions = {
                TextButton(
                    onClick = { /* TODO: Handle edit click */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        )
    }
}