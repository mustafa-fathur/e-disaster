package com.example.e_disaster.ui.features.disaster_aid.add

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.AppDatePickerDialog
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterAidScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: AddDisasterAidViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AddAidUiState.Success -> {
                Toast.makeText(context, "Bantuan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            }
            is AddAidUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Data Bantuan",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        if (disasterId == null) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Disaster ID tidak tersedia.", modifier = Modifier.padding(16.dp))
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            AidForm(
                buttonText = "Simpan",
                isLoading = uiState is AddAidUiState.Loading,
                onFormSubmit = { title, description, category, quantity, unit, donator, location, date, imageUri ->
                    val quantityInt = quantity.toIntOrNull()
                    if (title.isBlank() || description.isBlank() || category.isBlank() || quantityInt == null || unit.isBlank()) {
                        Toast.makeText(context, "Lengkapi semua field dengan benar", Toast.LENGTH_LONG).show()
                        return@AidForm
                    }

                    // Panggil ViewModel untuk submit data
                    // viewModel.submit(...)
                    println("Submitting: $title, $description, $category, $quantityInt, $unit, $donator, $location, $date")
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AidForm(
    buttonText: String,
    isLoading: Boolean = false,
    onFormSubmit: (
        title: String, description: String, category: String, quantity: String, unit: String,
        donator: String, location: String, date: String, imageUri: Uri?
    ) -> Unit
) {
    // States for form fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var donator by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var distributionDate by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Dropdown options
    val categoryOptions = listOf("Makanan", "Pakaian", "Fasilitas", "Obat-obatan")
    val unitOptions = listOf("Paket", "Set", "Kg", "Liter", "Box")

    // Date picker states
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Image picker launcher
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    fun onDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            distributionDate = simpleDateFormat.format(Date(it))
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            datePickerState = datePickerState,
            onDismiss = { showDatePicker = false },
            onConfirm = {
                onDateSelected(datePickerState.selectedDateMillis)
                showDatePicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- FORM FIELDS ---

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Judul Bantuan") },
            placeholder = { Text("Contoh: Paket Sembako") },
            leadingIcon = { Icon(Icons.Default.Title, contentDescription = "Judul") },
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Deskripsi") },
            placeholder = { Text("Jelaskan detail bantuan...") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = "Deskripsi") }
        )

        var isCategoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(expanded = isCategoryExpanded, onExpandedChange = { isCategoryExpanded = it }) {
            OutlinedTextField(
                value = category, onValueChange = {}, readOnly = true,
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                label = { Text("Kategori") },
                placeholder = { Text("Pilih kategori") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = "Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) }
            )
            ExposedDropdownMenu(expanded = isCategoryExpanded, onDismissRequest = { isCategoryExpanded = false }) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option) }, onClick = { category = option; isCategoryExpanded = false })
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = quantity, onValueChange = { quantity = it },
                modifier = Modifier.weight(1f),
                label = { Text("Jumlah") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = "Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )
            var isUnitExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = isUnitExpanded, onExpandedChange = { isUnitExpanded = it }, modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = unit, onValueChange = {}, readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    label = { Text("Satuan") },
                    placeholder = { Text("Pilih") },
                    leadingIcon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Satuan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUnitExpanded) }
                )
                ExposedDropdownMenu(expanded = isUnitExpanded, onDismissRequest = { isUnitExpanded = false }) {
                    unitOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = { unit = option; isUnitExpanded = false })
                    }
                }
            }
        }

        OutlinedTextField(
            value = donator, onValueChange = { donator = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Donatur") },
            placeholder = { Text("Contoh: Kemensos RI") },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Donatur") },
            singleLine = true
        )

        OutlinedTextField(
            value = location, onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Lokasi Distribusi") },
            placeholder = { Text("Contoh: Posko Cianjur") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Lokasi") },
            singleLine = true
        )

        OutlinedTextField(
            value = distributionDate, onValueChange = {},
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
            label = { Text("Tanggal Penyaluran") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Tanggal") },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )

        OutlinedTextField(
            value = imageUri?.lastPathSegment ?: "", onValueChange = {}, readOnly = true,
            modifier = Modifier.fillMaxWidth().clickable { pickImageLauncher.launch("image/*") },
            label = { Text("Foto Bantuan") },
            placeholder = { Text("Upload Foto") },
            leadingIcon = { Icon(Icons.Default.Photo, contentDescription = "Foto") },
            trailingIcon = {
                IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload")
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onFormSubmit(title, description, category, quantity, unit, donator, location, distributionDate, imageUri) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
            } else {
                Text(text = buttonText, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDisasterAidScreenPreview() {
    EDisasterTheme {
        AddDisasterAidScreen(navController = rememberNavController(), disasterId = "123")
    }
}
