package com.example.e_disaster.ui.features.disaster_report

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.ArrayList

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddDisasterReportScreen(
    navController: NavController,
    disasterId: String?
) {
    val viewModel: AddDisasterReportViewModel = hiltViewModel()
    val formState = viewModel.formState
    val requestState = viewModel.requestState

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showPreviewDialog by remember { mutableStateOf<Uri?>(null) }

    val imageUrisFromCamera = navController.currentBackStackEntry
        ?.savedStateHandle?.get<ArrayList<Uri>>("image_uris")

    LaunchedEffect(imageUrisFromCamera) {
        imageUrisFromCamera?.let {
            viewModel.addImageUris(it)
            navController.currentBackStackEntry?.savedStateHandle?.remove<ArrayList<Uri>>("image_uris")
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            viewModel.addImageUris(uris)
        }
    )

    val cameraPermissionState = rememberPermissionState(
        Manifest.permission.CAMERA,
        onPermissionResult = { isGranted ->
            if (isGranted) {
                navController.navigate("camera")
            }
        }
    )

    LaunchedEffect(requestState.isSuccess) {
        if (requestState.isSuccess) {
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("report_added", true)
            navController.popBackStack()
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Pilih Sumber Gambar") },
            text = { Text("Pilih dari galeri atau ambil foto baru?") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Text("Galeri")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    if (cameraPermissionState.status.isGranted) {
                        navController.navigate("camera")
                    } else {
                        cameraPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text("Kamera")
                }
            }
        )
    }

    if (showPreviewDialog != null) {
        Dialog(
            onDismissRequest = { showPreviewDialog = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                AsyncImage(
                    model = showPreviewDialog,
                    contentDescription = "Full screen image preview",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showPreviewDialog = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close preview", tint = Color.White)
                }
            }
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
        AddDisasterReportContent(
            modifier = Modifier.padding(innerPadding),
            formState = formState,
            errorMessage = requestState.errorMessage,
            onTitleChange = viewModel::onTitleChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onIsFinalChange = viewModel::onIsFinalChange,
            onLatChange = viewModel::onLatChange,
            onLongChange = viewModel::onLongChange,
            onPickImages = { showImageSourceDialog = true },
            onRemoveImage = viewModel::removeImageUri,
            onPreviewImage = { uri -> showPreviewDialog = uri },
            isSaving = requestState.isLoading,
            canSave = viewModel.canSave,
            onSave = { viewModel.createReport(disasterId) }
        )
    }
}

@Composable
fun AddDisasterReportContent(
    modifier: Modifier = Modifier,
    formState: AddReportFormState,
    errorMessage: String?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onIsFinalChange: (Boolean) -> Unit,
    onLatChange: (String) -> Unit,
    onLongChange: (String) -> Unit,
    onPickImages: () -> Unit,
    onRemoveImage: (Uri) -> Unit,
    onPreviewImage: (Uri) -> Unit,
    isSaving: Boolean,
    canSave: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
        ) {
            // Title
            OutlinedTextField(
                value = formState.title,
                onValueChange = onTitleChange,
                label = { Text("Judul Laporan") },
                modifier = Modifier.fillMaxWidth(),
                isError = formState.titleError != null,
                supportingText = { if (formState.titleError != null) Text(formState.titleError) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Description
            OutlinedTextField(
                value = formState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                isError = formState.descriptionError != null,
                supportingText = { if (formState.descriptionError != null) Text(formState.descriptionError) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Lat & Long
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = formState.lat,
                    onValueChange = onLatChange,
                    label = { Text("Latitude") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.latError != null,
                    supportingText = { if (formState.latError != null) Text(formState.latError) }
                )
                OutlinedTextField(
                    value = formState.long,
                    onValueChange = onLongChange,
                    label = { Text("Longitude") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = formState.longError != null,
                    supportingText = { if (formState.longError != null) Text(formState.longError) }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(onClick = onPickImages) {
                Text(if (formState.imageUris.isNotEmpty()) "Tambah Gambar Lain" else "Pilih Gambar")
            }

            if (formState.imageUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(formState.imageUris) { uri ->
                        Box(modifier = Modifier.size(96.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = "Selected image",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small)
                                    .clickable { onPreviewImage(uri) },
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onRemoveImage(uri) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove Image",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = formState.isFinal, onCheckedChange = onIsFinalChange)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ini adalah laporan tahap akhir")
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onSave,
            enabled = canSave,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menyimpan...")
            } else {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDisasterReportPreview() {
    EDisasterTheme {
        AddDisasterReportContent(
            modifier = Modifier.padding(16.dp),
            formState = AddReportFormState(
                title = "",
                description = "",
                titleError = "Judul tidak boleh kosong",
                latError = "Latitude harus angka"
            ),
            errorMessage = "This is a generic error message",
            onTitleChange = {},
            onDescriptionChange = {},
            onIsFinalChange = {},
            onLatChange = {},
            onLongChange = {},
            onPickImages = {},
            onRemoveImage = {},
            onPreviewImage = {},
            isSaving = false,
            canSave = true,
            onSave = {}
        )
    }
}
