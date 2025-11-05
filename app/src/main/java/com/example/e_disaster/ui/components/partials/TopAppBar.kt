package com.example.e_disaster.ui.components.partials

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.e_disaster.R
import com.example.e_disaster.ui.theme.EDisasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit = {},
    actions: @Composable (() -> Unit)? = null
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (canNavigateBack) {
                // Show Back Arrow
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali"
                    )
                }
            } else {
                // Show App Logo
                IconButton(onClick = { /* Logo is not clickable for now */ }) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
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
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile"
                        )
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
            title = "Beranda",
            canNavigateBack = false,
            onNavigateUp = {}
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
            onNavigateUp = {}
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
            onNavigateUp = {},
            actions = {
                TextButton(
                    onClick = { /* TODO: Handle edit click */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Ubah",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        )
    }
}