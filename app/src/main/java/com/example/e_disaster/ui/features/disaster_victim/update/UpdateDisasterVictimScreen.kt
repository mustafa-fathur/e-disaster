package com.example.e_disaster.ui.features.disaster_victim.update

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster_victim.add.AddVictimFormEvent
import com.example.e_disaster.ui.features.disaster_victim.add.VictimForm

@Composable
fun UpdateDisasterVictimScreen(
    navController: NavController,
    disasterId: String?,
    victimId: String?,
    viewModel: UpdateDisasterVictimViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(disasterId, victimId) {
        if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
            viewModel.loadInitialData(disasterId, victimId)
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Data korban berhasil diperbarui", Toast.LENGTH_SHORT).show()
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("victim_updated", true)
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null && uiState.name.isBlank() -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    val formUiState = uiState.toAddVictimUiState()

                    VictimForm(
                        uiState = formUiState,
                        onEvent = { event ->
                            viewModel.onEvent(event.toUpdateVictimFormEvent() ?: return@VictimForm)
                        },
                        onFormSubmit = {
                            if (disasterId != null && victimId != null) {
                                viewModel.submitUpdate(disasterId, victimId)
                            }
                        },
                        buttonText = "Simpan Perubahan",
                        showImagePicker = false
                    )

                    if (uiState.isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

private fun UpdateVictimUiState.toAddVictimUiState(): com.example.e_disaster.ui.features.disaster_victim.add.AddVictimUiState {
    return com.example.e_disaster.ui.features.disaster_victim.add.AddVictimUiState(
        name = this.name,
        nik = this.nik,
        dob = this.dob,
        gender = this.gender,
        contact = this.contact,
        description = this.description,
        victimStatus = this.victimStatus,
        isEvacuated = this.isEvacuated,
        isLoading = this.isLoading || this.isUpdating
    )
}

private fun AddVictimFormEvent.toUpdateVictimFormEvent(): UpdateVictimFormEvent? {
    return when (this) {
        is AddVictimFormEvent.NameChanged -> UpdateVictimFormEvent.NameChanged(this.name)
        is AddVictimFormEvent.NikChanged -> UpdateVictimFormEvent.NikChanged(this.nik)
        is AddVictimFormEvent.DobChanged -> UpdateVictimFormEvent.DobChanged(this.dob)
        is AddVictimFormEvent.GenderChanged -> UpdateVictimFormEvent.GenderChanged(this.gender)
        is AddVictimFormEvent.ContactChanged -> UpdateVictimFormEvent.ContactChanged(this.contact)
        is AddVictimFormEvent.DescriptionChanged -> UpdateVictimFormEvent.DescriptionChanged(this.description)
        is AddVictimFormEvent.StatusChanged -> UpdateVictimFormEvent.StatusChanged(this.status)
        is AddVictimFormEvent.IsEvacuatedChanged -> UpdateVictimFormEvent.IsEvacuatedChanged(this.isEvacuated)
        is AddVictimFormEvent.ImagesAdded, is AddVictimFormEvent.ImageRemoved -> null
    }
}
