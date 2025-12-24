package com.example.e_disaster.ui.features.disaster_aid.update

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme

@Composable
fun UpdateDisasterAidScreen(
    navController: NavController,
    disasterId: String?,
    aidId: String?,
    viewModel: UpdateDisasterAidViewModel = hiltViewModel()
) {
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(disasterId, aidId) {
        if (!disasterId.isNullOrEmpty() && !aidId.isNullOrEmpty()) {
            viewModel.loadInitialData(disasterId, aidId)
        }
    }

    LaunchedEffect(formState.isSuccess) {
        if (formState.isSuccess) {
            Toast.makeText(context, "Data bantuan berhasil diperbarui", Toast.LENGTH_SHORT).show()
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("aid_updated", true)
            navController.popBackStack()
        }
    }

    LaunchedEffect(formState.errorMessage) {
        formState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Edit Data Bantuan",
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
                formState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                formState.errorMessage != null && formState.title.isBlank() -> {
                    Text(
                        text = formState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    UpdateAidForm(
                        formState = formState,
                        onEvent = viewModel::onEvent,
                        onFormSubmit = {
                            if (disasterId != null && aidId != null) {
                                viewModel.submitUpdate(disasterId, aidId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UpdateAidForm(
    formState: UpdateAidFormState,
    onEvent: (UpdateAidFormEvent) -> Unit,
    onFormSubmit: () -> Unit
) {
    val categoryOptions = mapOf(
        "food" to "Pangan",
        "clothing" to "Sandang",
        "housing" to "Papan"
    )

    var categoryExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Validation error
        formState.validationError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Title
        OutlinedTextField(
            value = formState.title,
            onValueChange = { onEvent(UpdateAidFormEvent.TitleChanged(it)) },
            label = { Text("Judul Bantuan") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Title, contentDescription = "Title")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = categoryOptions[formState.category] ?: "Pilih Kategori",
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Category, contentDescription = "Category")
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categoryOptions.forEach { (key, value) ->
                    DropdownMenuItem(
                        text = { Text(value) },
                        onClick = {
                            onEvent(UpdateAidFormEvent.CategoryChanged(key))
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Quantity
        OutlinedTextField(
            value = formState.quantity,
            onValueChange = { onEvent(UpdateAidFormEvent.QuantityChanged(it)) },
            label = { Text("Jumlah") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Numbers, contentDescription = "Quantity")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Unit
        OutlinedTextField(
            value = formState.unit,
            onValueChange = { onEvent(UpdateAidFormEvent.UnitChanged(it)) },
            label = { Text("Satuan (contoh: kg, paket, box)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Description
        OutlinedTextField(
            value = formState.description,
            onValueChange = { onEvent(UpdateAidFormEvent.DescriptionChanged(it)) },
            label = { Text("Deskripsi") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Description, contentDescription = "Description")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Donator
        OutlinedTextField(
            value = formState.donator,
            onValueChange = { onEvent(UpdateAidFormEvent.DonatorChanged(it)) },
            label = { Text("Donatur/Penyumbang") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Star, contentDescription = "Donator")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Location
        OutlinedTextField(
            value = formState.location,
            onValueChange = { onEvent(UpdateAidFormEvent.LocationChanged(it)) },
            label = { Text("Lokasi Distribusi") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        // Submit Button
        Button(
            onClick = onFormSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !formState.isUpdating
        ) {
            if (formState.isUpdating) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(if (formState.isUpdating) "Menyimpan..." else "Simpan Perubahan")
        }
    }
}

@Preview(showBackground = true, name = "Update Aid Screen Light")
@Composable
fun UpdateDisasterAidLightPreview() {
    EDisasterTheme(darkTheme = false) {
        UpdateDisasterAidScreen(
            navController = rememberNavController(),
            disasterId = "123",
            aidId = "a1"
        )
    }
}
