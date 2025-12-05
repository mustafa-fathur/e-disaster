package com.example.e_disaster.ui.features.disaster_aid.update

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.AppDatePickerDialog
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.features.disaster.detail.AidItem
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun UpdateDisasterAidScreen(navController: NavController, aidId: String?) {
    // Dummy data source to find the aid to be updated
    val dummyAids = listOf(
        AidItem(
            id = "a1",
            title = "Paket Sembako",
            amount = "150 Paket",
            description = "Beras 10kg, mie instan, minyak goreng, gula",
            category = "food"
        ),
        AidItem(
            id = "a2",
            title = "Pakaian Layak Pakai",
            amount = "200 set",
            description = "Pakaian bekas layak pakai untuk dewasa dan anak-anak",
            category = "clothing"
        )
    )

    // In a real app, you would use the viewModel to fetch the aid details by aidId.
    val aidToUpdate = remember(aidId) {
        dummyAids.find { it.id == aidId }
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (aidToUpdate != null) {
                // We use the newly created AidForm and pass the initial data
                AidForm(
                    buttonText = "Simpan Perubahan",
                    initialTitle = aidToUpdate.title,
                    initialDescription = aidToUpdate.description,
                    initialCategory = when(aidToUpdate.category) {
                        "food" -> "Makanan"
                        "clothing" -> "Pakaian"
                        "housing" -> "Fasilitas"
                        "medicine" -> "Obat-obatan"
                        else -> ""
                    },
                    initialQuantity = aidToUpdate.amount.split(" ").firstOrNull() ?: "",
                    initialUnit = aidToUpdate.amount.split(" ").getOrNull(1) ?: "",
                    onFormSubmit = { title, description, category, quantity, unit, donator, location, date, photoUri ->
                        // TODO: Implement ViewModel logic to update the aid data
                        println("Updating Aid ID $aidId with new data...")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AidForm(
    buttonText: String,
    initialTitle: String = "",
    initialDescription: String = "",
    initialCategory: String = "",
    initialQuantity: String = "",
    initialUnit: String = "",
    initialDonator: String = "Kemensos RI", // placeholder
    initialLocation: String = "Posko Utama Cianjur", // placeholder
    initialDate: String = "29/10/2025", // placeholder
    onFormSubmit: (
        title: String,
        description: String,
        category: String,
        quantity: String,
        unit: String,
        donator: String,
        location: String,
        date: String,
        photoUri: String
    ) -> Unit
) {
    // States for form fields
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }
    var category by remember { mutableStateOf(initialCategory) }
    var quantity by remember { mutableStateOf(initialQuantity) }
    var unit by remember { mutableStateOf(initialUnit) }
    var donator by remember { mutableStateOf(initialDonator) }
    var location by remember { mutableStateOf(initialLocation) }
    var distributionDate by remember { mutableStateOf(initialDate) }

    // Dropdown options
    val categoryOptions = listOf("Makanan", "Pakaian", "Fasilitas", "Obat-obatan")
    val unitOptions = listOf("Paket", "Set", "Kg", "Liter", "Box")

    // Date picker states
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

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

        // Category Dropdown
        var isCategoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isCategoryExpanded,
            onExpandedChange = { isCategoryExpanded = it }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Kategori") },
                placeholder = { Text("Pilih kategori") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = "Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryExpanded) }
            )
            ExposedDropdownMenu(
                expanded = isCategoryExpanded,
                onDismissRequest = { isCategoryExpanded = false }
            ) {
                categoryOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            category = option
                            isCategoryExpanded = false
                        }
                    )
                }
            }
        }

        // Quantity and Unit Row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                modifier = Modifier.weight(1f),
                label = { Text("Jumlah") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = "Jumlah") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                singleLine = true
            )
            var isUnitExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = isUnitExpanded,
                onExpandedChange = { isUnitExpanded = it },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = unit,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(),
                    label = { Text("Satuan") },
                    placeholder = { Text("Pilih") },
                    leadingIcon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Satuan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isUnitExpanded) }
                )
                ExposedDropdownMenu(expanded = isUnitExpanded, onDismissRequest = { isUnitExpanded = false }) {
                    unitOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                unit = option
                                isUnitExpanded = false
                            }
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = donator,
            onValueChange = { donator = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Donatur") },
            placeholder = { Text("Contoh: Kemensos RI") },
            leadingIcon = { Icon(Icons.Default.Star, contentDescription = "Donatur") },
            singleLine = true
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Lokasi Distribusi") },
            placeholder = { Text("Contoh: Posko Cianjur") },
            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Lokasi") },
            singleLine = true
        )

        OutlinedTextField(
            value = distributionDate,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
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
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Implement image picker */ },
            label = { Text("Foto Bantuan") },
            placeholder = { Text("Upload Foto") },
            leadingIcon = { Icon(Icons.Default.Photo, contentDescription = "Foto") },
            trailingIcon = {
                IconButton(onClick = { /* TODO: Implement image picker */ }) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload")
                }
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onFormSubmit(title, description, category, quantity, unit, donator, location, distributionDate, "") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = buttonText, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Preview(showBackground = true, name = "Update Aid Screen Light")
@Composable
fun UpdateDisasterAidLightPreview() {
    EDisasterTheme(darkTheme = false) {
        UpdateDisasterAidScreen(navController = rememberNavController(), aidId = "a1")
    }
}
