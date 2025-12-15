package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.ui.theme.EDisasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UNUSED_PARAMETER")
@Composable
fun AddDisasterReportScreen(navController: NavController, disasterId: String?) {
    // ViewModel
    val viewModel: AddDisasterReportViewModel = hiltViewModel()
    val uiState = viewModel.uiState

    // Move state declarations here
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isFinal by remember { mutableStateOf(false) }

    // whether saving is allowed (disasterId must be present and not blank)
    val canSave = !disasterId.isNullOrBlank()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Perkembangan Bencana",
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
            AddDisasterReportContent(
                modifier = Modifier.fillMaxWidth(),
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                isFinal = isFinal,
                onIsFinalChange = { isFinal = it },
                isSaving = uiState.isLoading,
                canSave = canSave,
                onSave = { t, d, f ->
                    viewModel.createReport(disasterId, t, d, f)
                }
            )

            if (!canSave) {
                Text(
                    text = "ID bencana tidak tersedia. Tidak dapat menyimpan laporan.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else if (uiState.errorMessage != null) {
                Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterReportContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isFinal: Boolean,
    onIsFinalChange: (Boolean) -> Unit,
    isSaving: Boolean = false,
    canSave: Boolean = true,
    onSave: (title: String, description: String, isFinal: Boolean) -> Unit = { _, _, _ -> }
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Judul
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(text = "Judul Laporan") },
            placeholder = { Text(text = "Judul Laporan") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = "Deskripsi") },
            placeholder = { Text(text = "Deskripsi") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
        )

        // Final report checkbox
        androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFinal, onCheckedChange = onIsFinalChange)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ini adalah laporan tahap akhir")
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save button (match victim screen style)
        Button(
            onClick = { onSave(title, description, isFinal) },
            enabled = canSave && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Menyimpan...", style = MaterialTheme.typography.titleMedium)
            } else {
                Text(text = "Simpan", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDisasterReportPreview() {
    EDisasterTheme {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var isFinal by remember { mutableStateOf(false) }

        Column(modifier = Modifier.padding(16.dp)) {
            AddDisasterReportContent(
                modifier = Modifier.fillMaxWidth(),
                title = title,
                onTitleChange = {},
                description = description,
                onDescriptionChange = {},
                isFinal = isFinal,
                onIsFinalChange = {},
                canSave = true,
                onSave = { _, _, _ -> }
            )
        }
    }
}
