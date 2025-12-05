package com.example.e_disaster.ui.features.disaster_victim.update

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster.detail.VictimItem
import com.example.e_disaster.ui.features.disaster_victim.add.VictimForm
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun UpdateDisasterVictimScreen(navController: NavController, victimId: String?) {
    val dummyVictims = listOf(
        VictimItem("1", "Siti Rahayu", "Luka ringan di kaki kanan akibat reruntuhan", "minor_injury", true),
        VictimItem("2", "Budi Santoso", "Mengalami dehidrasi", "minor_injury", false),
        VictimItem("3", "Ahmad Subarjo", "Patah tulang di lengan kiri", "serious_injuries", true)
    )

    val victimToUpdate = remember(victimId) {
        dummyVictims.find { it.id == victimId }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Edit Data Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (victimToUpdate != null) {
                // We reuse VictimForm from the 'add' screen
                VictimForm(
                    buttonText = "Simpan Perubahan", // Updated button text
                    initialName = victimToUpdate.name,
                    initialNik = "123131321321321321", // Dummy NIK
                    initialDob = "15/05/1965", // Dummy DOB
                    initialGender = "Perempuan", // Dummy Gender
                    initialContact = "0821321321", // Dummy Contact
                    initialDescription = victimToUpdate.description,
                    initialStatus = when (victimToUpdate.status) { // Map status key to display text
                        "minor_injury" -> "Luka Ringan"
                        "serious_injuries" -> "Luka Berat"
                        // Add other mappings as needed
                        else -> "Lainnya"
                    },
                    initialIsEvacuated = victimToUpdate.isEvacuated,
                    onFormSubmit = { name, nik, dob, gender, contact, description, status, photoUri, isEvacuated ->
                        // TODO: Implement ViewModel logic to update the victim
                        println("Updating Victim ID $victimId: Name=$name, NIK=$nik, DOB=$dob, Gender=$gender, Contact=$contact, Desc=$description, Status=$status, isEvacuated=$isEvacuated")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Update Victim Light")
@Composable
fun UpdateDisasterVictimLightPreview() {
    EDisasterTheme(darkTheme = false) {
        UpdateDisasterVictimScreen(navController = rememberNavController(), victimId = "1")
    }
}

@Preview(showBackground = true, name = "Update Victim Dark")
@Composable
fun UpdateDisasterVictimDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        UpdateDisasterVictimScreen(navController = rememberNavController(), victimId = "1")
    }
}