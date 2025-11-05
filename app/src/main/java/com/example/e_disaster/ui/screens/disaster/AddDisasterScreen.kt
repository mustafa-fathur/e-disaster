package com.example.e_disaster.ui.screens.disaster

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterScreen(navController: NavController) {
    // States for form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var magnitude by remember { mutableStateOf("") }
    var depth by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    // State for disaster type dropdown
    var typeExpanded by remember { mutableStateOf(false) }
    var disasterType by remember { mutableStateOf("earthquake") } // Default value
    val disasterTypesOptions = listOf(
        "earthquake", "tsunami", "volcanic_eruption", "flood", "drought",
        "tornado", "landslide", "non_natural_disaster", "social_disaster"
    )

    // State for status dropdown
    var statusExpanded by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("ongoing") }
    val statusOptions = listOf("ongoing", "completed", "cancelled")

    // State for image picker
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    // Date and Time Picker Dialogs
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            date = "$selectedYear-${selectedMonth + 1}-$selectedDay"
        }, year, month, day
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            time = String.format("%02d:%02d:00", selectedHour, selectedMinute)
        }, hour, minute, true
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text("Formulir Tambah Bencana", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Bencana") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
            }

            // Disaster Type Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded }
                ) {
                    OutlinedTextField(
                        value = disasterType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipe Bencana") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        disasterTypesOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.replace('_', ' ').replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    disasterType = option
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = location,

                    onValueChange = { location = it },
                    label = { Text("Lokasi (cth: Jakarta, Indonesia)") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            // Latitude and Longitude
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = latitude,
                        onValueChange = { latitude = it },
                        label = { Text("Latitude") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = longitude,
                        onValueChange = { longitude = it },
                        label = { Text("Longitude") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // Magnitude and Depth
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = magnitude,
                        onValueChange = { magnitude = it },
                        label = { Text("Magnitudo") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = depth,
                        onValueChange = { depth = it },
                        label = { Text("Kedalaman (km)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            // Date and Time Pickers
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { datePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                        Text(if (date.isEmpty()) "Pilih Tanggal" else date)
                    }
                    Button(onClick = { timePickerDialog.show() }, modifier = Modifier.weight(1f)) {
                        Text(if (time.isEmpty()) "Pilih Waktu" else time)
                    }
                }
            }

            // Image picker
            item {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
                    Button(onClick = { launcher.launch("image/*") }) {
                        Text("Pilih Gambar")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    imageBitmap?.let { bmp ->
                        Image(
                            bitmap = bmp.asImageBitmap(),
                            contentDescription = "Preview gambar",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                }
            }

            // Status Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = status,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statusOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    status = option
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // TODO: Implement the logic to create a data object from all the states
                        // and send it to the ViewModel for API call.
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Simpan Bencana")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDisasterScreenPreview() {
    val navController = rememberNavController()
    EDisasterTheme {
        AddDisasterScreen(navController = navController)
    }
}
