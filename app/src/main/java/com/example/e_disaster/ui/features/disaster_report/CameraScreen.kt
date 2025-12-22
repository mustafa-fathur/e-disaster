package com.example.e_disaster.ui.features.disaster_report

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.io.File
import java.util.ArrayList

@Composable
fun CameraScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    var takenImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showPreviewDialog by remember { mutableStateOf<Uri?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Camera Preview
        AndroidView(
            factory = {
                PreviewView(it).apply {
                    this.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar with Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(16.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
            }
        }

        // Bottom Controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            // Thumbnail gallery
            if (takenImageUris.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(takenImageUris) { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Captured image thumbnail",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(MaterialTheme.shapes.small)
                                .clickable { showPreviewDialog = uri },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                // Placeholder to keep the height consistent
                Spacer(modifier = Modifier.height(88.dp))
            }

            // Action buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Left Spacer for balancing
                Spacer(modifier = Modifier.size(64.dp))

                // Take Picture Button
                IconButton(
                    onClick = {
                        takePicture(context, cameraController) { uri ->
                            takenImageUris = takenImageUris + uri
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .border(4.dp, Color.White, CircleShape)
                ) {
                    // This creates the inner circle of the shutter button
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .background(Color.White, CircleShape)
                    )
                }

                // Done Button
                if (takenImageUris.isNotEmpty()) {
                    Card(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                        onClick = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("image_uris", ArrayList(takenImageUris))
                            navController.popBackStack()
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(Icons.Default.Check, contentDescription = "Selesai", tint = Color.White)
                        }
                    }
                } else {
                    // Placeholder to keep the take picture button centered
                    Spacer(modifier = Modifier.size(64.dp))
                }
            }
        }
    }

    // Fullscreen Preview Dialog
    if (showPreviewDialog != null) {
        Dialog(
            onDismissRequest = { showPreviewDialog = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                AsyncImage(
                    model = showPreviewDialog,
                    contentDescription = "Preview gambar yang diambil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showPreviewDialog = null },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup pratinjau", tint = Color.White)
                }
            }
        }
    }
}

private fun takePicture(
    context: Context,
    cameraController: CameraController,
    onResult: (Uri) -> Unit
) {
    val file = context.createImageFile()
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()
    cameraController.takePicture(
        outputDirectory,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                outputFileResults.savedUri?.let { onResult(it) }
            }

            override fun onError(exception: ImageCaptureException) {
                println("Error taking picture: ${exception.message}")
            }
        }
    )
}

private fun Context.createImageFile(): File {
    val timeStamp = System.currentTimeMillis()
    val imageFileName = "JPEG_" + timeStamp + "_"
    return File.createTempFile(
        imageFileName, /* prefix */
        ".jpg", /* suffix */
        externalCacheDir      /* directory */
    )
}
