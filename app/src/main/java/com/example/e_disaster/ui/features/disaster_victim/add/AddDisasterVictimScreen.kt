package com.example.e_disaster.ui.features.disaster_victim.add

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.BuildConfig
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.AppDatePickerDialog
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddDisasterVictimScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: AddDisasterVictimViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Data korban berhasil ditambahkan", Toast.LENGTH_SHORT).show()
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

    LaunchedEffect(uiState.validationError) {
        uiState.validationError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Data Korban",
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
            VictimForm(
                uiState = uiState,
                onEvent = viewModel::onEvent,
                onFormSubmit = {
                    if (disasterId != null) {
                        viewModel.submitForm(disasterId, context)
                    } else {
                        Toast.makeText(context, "ID Bencana tidak valid.", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VictimForm(
    uiState: AddVictimUiState,
    onEvent: (AddVictimFormEvent) -> Unit,
    onFormSubmit: () -> Unit
) {
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris -> onEvent(AddVictimFormEvent.ImagesAdded(uris)) }
    )

    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                val uri = tempCameraImageUri
                if (uri != null) {
                    onEvent(AddVictimFormEvent.ImagesAdded(listOf(uri)))
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newImageFile =
                    File(context.filesDir, "temp_image_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", newImageFile)
                tempCameraImageUri = uri
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    )

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    fun onDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy",
                Locale.getDefault())
            onEvent(AddVictimFormEvent.DobChanged(simpleDateFormat.format(Date(it))))
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

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Pilih gambar dari galeri atau ambil foto baru menggunakan kamera.") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Kamera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { onEvent(AddVictimFormEvent.NameChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nama Lengkap*") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            isError = uiState.validationError != null && uiState.name.isBlank()
        )

        OutlinedTextField(
            value = uiState.nik,
            onValueChange = { onEvent(AddVictimFormEvent.NikChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("NIK*") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.id_card), contentDescription = "NIK", modifier = Modifier.size(24.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            singleLine = true,
            isError = uiState.validationError != null && uiState.nik.isBlank()
        )

        OutlinedTextField(
            value = uiState.dob,
            onValueChange = { /* Dikelola oleh Date Picker */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            label = { Text("Tanggal Lahir*") },
            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Tanggal Lahir") },
            readOnly = true,
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = if (uiState.validationError != null && uiState.dob.isBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        GenderSelector(
            selectedGender = uiState.gender,
            onGenderSelected = { onEvent(AddVictimFormEvent.GenderChanged(it)) }
        )

        OutlinedTextField(
            value = uiState.contact,
            onValueChange = { onEvent(AddVictimFormEvent.ContactChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Kontak") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Kontak") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.description,
            onValueChange = { onEvent(AddVictimFormEvent.DescriptionChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Deskripsi") },
            leadingIcon = { Icon(painter = painterResource(R.drawable.text_fields), contentDescription = "Deskripsi", modifier = Modifier.size(24.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
            minLines = 3
        )

        var isStatusExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = isStatusExpanded,
            onExpandedChange = { isStatusExpanded = it }
        ) {
            OutlinedTextField(
                value = uiState.victimStatus,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Status Korban*") },
                leadingIcon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Status Korban") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusExpanded) },
                isError = uiState.validationError != null && uiState.victimStatus.isBlank()
            )
            ExposedDropdownMenu(
                expanded = isStatusExpanded,
                onDismissRequest = { isStatusExpanded = false }
            ) {
                listOf("Luka Ringan", "Luka Berat", "Meninggal", "Hilang").forEach { status ->
                    DropdownMenuItem(
                        text = { Text(status) },
                        onClick = {
                            onEvent(AddVictimFormEvent.StatusChanged(status))
                            isStatusExpanded = false
                        }
                    )
                }
            }
        }

        Text("Foto Korban", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(uiState.images) { uri ->
                ImagePreviewItem(
                    uri = uri,
                    onRemoveClick = { onEvent(AddVictimFormEvent.ImageRemoved(uri)) }
                )
            }
            item {
                AddImageButton(onClick = { showImageSourceDialog = true })
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onEvent(AddVictimFormEvent.IsEvacuatedChanged(!uiState.isEvacuated)) }
        ) {
            Checkbox(
                checked = uiState.isEvacuated,
                onCheckedChange = { onEvent(AddVictimFormEvent.IsEvacuatedChanged(it)) }
            )
            Text("Sudah Dievakuasi", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFormSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = !uiState.isLoading
        ) {
            Text(text = "Simpan", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
fun GenderSelector(selectedGender: String, onGenderSelected: (String) -> Unit) {
    Column {
        Text("Jenis Kelamin*", style = MaterialTheme.typography.bodyLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            listOf("Laki-laki", "Perempuan").forEach { gender ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onGenderSelected(gender) }
                        .padding(end = 16.dp)
                ) {
                    RadioButton(
                        selected = (selectedGender == gender),
                        onClick = { onGenderSelected(gender) }
                    )
                    Text(text = gender, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Composable
fun AddImageButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline,
                androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Tambah Foto",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ImagePreviewItem(uri: Uri, onRemoveClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Image Preview",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus Gambar",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
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

