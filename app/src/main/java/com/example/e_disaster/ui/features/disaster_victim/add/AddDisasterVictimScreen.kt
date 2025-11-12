package com.example.e_disaster.ui.features.disaster_victim.add

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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.AppDatePickerDialog
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun AddDisasterVictimScreen(navController: NavController, disasterId: String?) {
    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Data Korban", // Title updated
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
            VictimForm(
                buttonText = "Simpan", // Button text updated
                onFormSubmit = { name, nik, dob, gender, contact, description, status, photoUri, isEvacuated ->
                    // TODO: Implement ViewModel logic to add victim with all new fields
                    println("Adding Victim: Name=$name, NIK=$nik, DOB=$dob, Gender=$gender, Contact=$contact, Desc=$description, Status=$status, isEvacuated=$isEvacuated to Disaster ID: $disasterId")
                    navController.popBackStack()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VictimForm(
    buttonText: String,
    initialName: String = "",
    initialNik: String = "",
    initialDob: String = "",
    initialGender: String = "Laki-laki",
    initialContact: String = "",
    initialDescription: String = "",
    initialStatus: String = "",
    initialIsEvacuated: Boolean = false,
    onFormSubmit: (
        name: String,
        nik: String,
        dob: String,
        gender: String,
        contact: String,
        description: String,
        status: String,
        photoUri: String,
        isEvacuated: Boolean
    ) -> Unit
) {
    var name by remember { mutableStateOf(initialName) }
    var nik by remember { mutableStateOf(initialNik) }
    var dob by remember { mutableStateOf(initialDob) }
    var gender by remember { mutableStateOf(initialGender) }
    var contact by remember { mutableStateOf(initialContact) }
    var description by remember { mutableStateOf(initialDescription) }
    var victimStatus by remember { mutableStateOf(initialStatus) }
    var isEvacuated by remember { mutableStateOf(initialIsEvacuated) }

    val statusOptions = listOf("Luka Ringan", "Luka Berat", "Meninggal", "Hilang")

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    fun onDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            dob = simpleDateFormat.format(Date(it))
        }
    }

    if (showDatePicker) {
        AppDatePickerDialog(
            datePickerState = datePickerState,
            onDismiss = {showDatePicker = false},
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
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama Lengkap") },
            placeholder = { Text("Nama") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = nik,
            onValueChange = { nik = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("NIK") },
            placeholder = { Text("NIK") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.id_card), contentDescription = "NIK") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = dob,
            onValueChange = { dob = it },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            label = { Text("Tanggal Lahir") },
            placeholder = { Text("dd/mm/yyyy") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Tanggal Lahir") },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface,
            )
        )

        GenderSelector(
            selectedGender = gender,
            onGenderSelected = { gender = it }
        )

        OutlinedTextField(
            value = contact,
            onValueChange = { contact = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Kontak") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Kontak") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Deskripsi") },
            placeholder = { Text("Jelaskan") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.text_fields), contentDescription = "Deskripsi") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done)
        )

        // Status Dropdown
        var isStatusExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isStatusExpanded,
            onExpandedChange = { isStatusExpanded = it }
        ) {
            OutlinedTextField(
                value = victimStatus,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Status Korban") },
                placeholder = { Text("Pilih status korban") },
                leadingIcon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Status Korban") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) }
            )
            ExposedDropdownMenu(
                expanded = isStatusExpanded,
                onDismissRequest = { isStatusExpanded = false },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                statusOptions.forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            victimStatus = status
                            isStatusExpanded = false
                        }
                    )
                }
            }
        }

        // Photo Upload Field
        OutlinedTextField(
            value = "", // This can be managed by a state that holds the file name
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { /* TODO: Implement image picker logic */ },
            label = { Text("Foto Bencana") },
            placeholder = { Text("Upload Foto") },
            leadingIcon = { Icon(Icons.Default.Photo, contentDescription = "Foto") },
            trailingIcon = {
                IconButton(onClick = { /* TODO: Implement image picker logic */ }) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload")
                }
            }
        )

        // Evacuation Checkbox
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isEvacuated = !isEvacuated }
        ) {
            Checkbox(
                checked = isEvacuated,
                onCheckedChange = { isEvacuated = it }
            )
            Text("Sudah Dievakuasi", style = MaterialTheme.typography.bodyLarge)
        }


        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onFormSubmit(name, nik, dob, gender, contact, description, victimStatus, "", isEvacuated) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(text = buttonText, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun GenderSelector(
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    val genders = listOf("Laki-laki", "Perempuan")
    Column {
        Text("Jenis Kelamin*", style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            genders.forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onGenderSelected(gender) }
                ) {
                    RadioButton(
                        selected = (gender == selectedGender),
                        onClick = { onGenderSelected(gender) }
                    )
                    Text(text = gender, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Add Victim Form Screen Light")
@Composable
fun VictimFormLight() {
    EDisasterTheme(darkTheme = false){
        AddDisasterVictimScreen(navController = rememberNavController(), disasterId = null)
    }
}

@Preview(showBackground = true, name = "Add Victim Form Screen Dark")
@Composable
fun VictimFormDark() {
    EDisasterTheme(darkTheme = true){
        AddDisasterVictimScreen(navController = rememberNavController(), disasterId = null)
    }
}

