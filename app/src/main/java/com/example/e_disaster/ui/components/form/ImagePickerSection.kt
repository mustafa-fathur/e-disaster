package com.example.e_disaster.ui.components.form

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.e_disaster.ui.components.dialogs.FullScreenImageViewer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

private fun createTempImageUri(context: android.content.Context): Uri {
    val newImageFile = File(context.cacheDir, "temp_camera_${System.currentTimeMillis()}.jpg")
    val authority = "${context.packageName}.provider"
    return FileProvider.getUriForFile(context, authority, newImageFile)
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImagePickerSection(
    modifier: Modifier = Modifier,
    uris: List<Uri>,
    onImagesAdded: (List<Uri>) -> Unit,
    onImageRemoved: (Uri) -> Unit,
    itemSize: Dp = 160.dp
) {
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var selectedImageForPreview by remember { mutableStateOf<Uri?>(null) }
    var tempCameraImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { newUris ->
            onImagesAdded(newUris)
        }
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                tempCameraImageUri?.let { uri -> onImagesAdded(listOf(uri)) }
            }
        }
    )

    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val newUri = createTempImageUri(context)
                tempCameraImageUri = newUri
                cameraLauncher.launch(newUri)
            } else {
                Toast.makeText(context, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    )

    if (showImageSourceDialog) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onCameraClick = {
                showImageSourceDialog = false
                if (cameraPermissionState.status.isGranted) {
                    val newUri = createTempImageUri(context)
                    tempCameraImageUri = newUri
                    cameraLauncher.launch(newUri)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onGalleryClick = {
                showImageSourceDialog = false
                galleryLauncher.launch("image/*")
            }
        )
    }

    selectedImageForPreview?.let { uri ->
        FullScreenImageViewer(
            imageUri = uri,
            onDismiss = { selectedImageForPreview = null }
        )
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Foto Korban (${uris.size})", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(uris) { uri ->
                ImagePreviewItem(
                    uri = uri,
                    onRemoveClick = { onImageRemoved(uri) },
                    onImageClick = { selectedImageForPreview = uri },
                    itemSize = itemSize
                )
            }
            item {
                AddImageButton(
                    onClick = { showImageSourceDialog = true },
                    itemSize = itemSize
                )
            }
        }
    }
}

@Composable
private fun AddImageButton(onClick: () -> Unit, itemSize: Dp) {
    Box(
        modifier = Modifier
            .size(itemSize)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Tambah Foto",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(itemSize / 3)
        )
    }
}

@Composable
private fun ImagePreviewItem(
    uri: Uri,
    onRemoveClick: () -> Unit,
    onImageClick: () -> Unit,
    itemSize: Dp
) {
    Box(
        modifier = Modifier
            .size(itemSize)
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "Image Preview",
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onImageClick),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onRemoveClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
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

@Composable
private fun ImageSourceDialog(onDismiss: () -> Unit, onCameraClick: () -> Unit, onGalleryClick: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Sumber Gambar") },
        text = { Text("Pilih gambar dari galeri atau ambil foto baru menggunakan kamera.") },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = { TextButton(onClick = onCameraClick) { Text("Kamera") } },
        dismissButton = { TextButton(onClick = onGalleryClick) { Text("Galeri") } }
    )
}

