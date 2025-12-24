package com.example.e_disaster.ui.features.disaster

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.form.ImagePickerSection
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDisasterScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: UpdateDisasterViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Load disaster data when screen opens
    LaunchedEffect(disasterId) {
        if (disasterId != null && uiState.disasterId != disasterId) {
            viewModel.loadDisaster(disasterId)
        }
    }

    // Disaster types
    val disasterTypes = listOf(
        "earthquake" to "Gempa Bumi",
        "tsunami" to "Tsunami",
        "volcanic_eruption" to "Gunung Meletus",
        "flood" to "Banjir",
        "drought" to "Kekeringan",
        "tornado" to "Angin Topan",
        "landslide" to "Tanah Longsor",
        "non_natural_disaster" to "Bencana Non Alam",
        "social_disaster" to "Bencana Sosial"
    )

    // Status options
    val statusOptions = listOf(
        "ongoing" to "Berlangsung",
        "completed" to "Selesai",
        "cancelled" to "Dibatalkan"
    )

    var typeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    // Handle events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UpdateDisasterEvent.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("disaster_updated", true)
                    navController.popBackStack()
                }
                is UpdateDisasterEvent.Error -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            viewModel.updateDate(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            val formattedTime = String.format("%02d:%02d", hour, minute)
            viewModel.updateTime(formattedTime)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Edit Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() })
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.isLoadingData) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    item {
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            label = { Text("Judul*") },
                            leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        OutlinedTextField(
                            value = uiState.description,
                            onValueChange = { viewModel.updateDescription(it) },
                            label = { Text("Deskripsi") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = typeExpanded,
                                onExpandedChange = { typeExpanded = !typeExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedTypeLabel,
                                    onValueChange = {},
                                    label = { Text("Jenis*") },
                                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                    readOnly = true,
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = typeExpanded,
                                    onDismissRequest = { typeExpanded = false }
                                ) {
                                    disasterTypes.forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                viewModel.updateType(value, label)
                                                typeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            ExposedDropdownMenuBox(
                                expanded = statusExpanded,
                                onExpandedChange = { statusExpanded = !statusExpanded },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedStatusLabel,
                                    onValueChange = {},
                                    label = { Text("Status") },
                                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                                    readOnly = true,
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = statusExpanded,
                                    onDismissRequest = { statusExpanded = false }
                                ) {
                                    statusOptions.forEach { (value, label) ->
                                        DropdownMenuItem(
                                            text = { Text(label) },
                                            onClick = {
                                                viewModel.updateStatus(value, label)
                                                statusExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        OutlinedTextField(
                            value = uiState.location,
                            onValueChange = { viewModel.updateLocation(it) },
                            label = { Text("Lokasi") },
                            leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = uiState.latitude,
                                onValueChange = { viewModel.updateLatitude(it) },
                                label = { Text("Latitude") },
                                leadingIcon = { Icon(Icons.Default.GpsFixed, contentDescription = null) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = uiState.longitude,
                                onValueChange = { viewModel.updateLongitude(it) },
                                label = { Text("Longitude") },
                                leadingIcon = { Icon(Icons.Default.GpsFixed, contentDescription = null) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedTextField(
                                value = uiState.date,
                                onValueChange = {},
                                label = { Text("Tanggal") },
                                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { datePickerDialog.show() },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            OutlinedTextField(
                                value = uiState.time,
                                onValueChange = {},
                                label = { Text("Waktu") },
                                leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                                readOnly = true,
                                enabled = false,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { timePickerDialog.show() },
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                    
                    // Kondisi untuk menampilkan Magnitudo dan Kedalaman
                    if (uiState.selectedType == "earthquake") {
                        item { Spacer(modifier = Modifier.height(16.dp)) }
                        item {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedTextField(
                                    value = uiState.magnitude,
                                    onValueChange = { viewModel.updateMagnitude(it) },
                                    label = { Text("Magnitudo") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = uiState.depth,
                                    onValueChange = { viewModel.updateDepth(it) },
                                    label = { Text("Kedalaman (km)") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true
                                )
                            }
                        }
                    }
                    
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    item {
                        ImagePickerSection(
                            uris = uiState.images,
                            onImagesAdded = { viewModel.addImages(it) },
                            onImageRemoved = { viewModel.removeImage(it) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                    item {
                        Button(
                            onClick = { viewModel.submitUpdate() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.height(24.dp)
                                )
                            } else {
                                Text("Simpan Perubahan", style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDisasterScreenPreview() {
    EDisasterTheme {
        UpdateDisasterScreen(rememberNavController(), "1")
    }
}
