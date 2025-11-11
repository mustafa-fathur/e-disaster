package com.example.e_disaster.ui.features.disaster_victim

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar

@Composable
fun DisasterVictimDetailScreen(navController: NavController, victimId: String?) {
    val dummyVictim = Victim("1", "Korban 1", "Deskripsi contoh", "2025-09-10")

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    TextButton(
                        onClick = {
                            navController.navigate("update-disaster-victim/$victimId")
                        }, colors = ButtonDefaults.textButtonColors(
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
                        Text("Ubah")
                    }
                }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ReadOnlyTextField(label = "NIK", value = "1234567890123456")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Nama", value = dummyVictim.name)
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Umur", value = "25")
            Spacer(modifier = Modifier.height(16.dp))
            ReadOnlyTextField(label = "Deskripsi", value = dummyVictim.description, lines = 5)
        }
    }
}

@Composable
private fun ReadOnlyTextField(label: String, value: String, lines: Int = 1) {
    Text(label, style = MaterialTheme.typography.labelMedium)
    OutlinedTextField(
        value = value,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        readOnly = true,
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        minLines = lines
    )
}
