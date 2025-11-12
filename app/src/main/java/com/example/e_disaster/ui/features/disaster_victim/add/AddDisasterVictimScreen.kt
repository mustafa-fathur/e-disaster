package com.example.e_disaster.ui.features.disaster_victim.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar

@Composable
fun AddDisasterVictimScreen(navController: NavController, disasterId: String?) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Korban Baru",
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
            VictimForm(
                buttonText = "Tambah",
                onFormSubmit = { nik, name, age, description ->
                    // TODO: Implement ViewModel logic to add victim
                    println("Adding Victim: NIK=$nik, Name=$name, Age=$age, Desc=$description to Disaster ID: $disasterId")
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun VictimForm(
    buttonText: String,
    initialNik: String = "",
    initialName: String = "",
    initialAge: String = "",
    initialDescription: String = "",
    onFormSubmit: (nik: String, name: String, age: String, description: String) -> Unit
) {
    var nik by remember { mutableStateOf(initialNik) }
    var name by remember { mutableStateOf(initialName) }
    var age by remember { mutableStateOf(initialAge) }
    var description by remember { mutableStateOf(initialDescription) }

    Column(modifier = Modifier.fillMaxSize()) {

        Text("NIK", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = nik,
            onValueChange = { nik = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Nama", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Umur", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text("Deskripsi", style = MaterialTheme.typography.labelMedium)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onFormSubmit(nik, name, age, description) },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = buttonText, style = MaterialTheme.typography.titleMedium)
        }
    }
}
