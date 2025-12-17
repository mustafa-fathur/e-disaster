package com.example.e_disaster.ui.features.disaster_victim.detail

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.ui.components.badges.DisasterEvacuationStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterVictimStatusBadge
import com.example.e_disaster.ui.components.form.ImagePickerSection
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DisasterVictimDetailScreen(
    navController: NavController,
    disasterId: String?,
    victimId: String?,
    viewModel: DisasterVictimDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteVictimDialog by remember { mutableStateOf(false) }
    var pictureToDelete by remember { mutableStateOf<VictimPicture?>(null) }

    val updateResultState = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<Boolean>("victim_updated")
        ?.observeAsState()

    val updateResult = updateResultState?.value

    LaunchedEffect(updateResult) {
        if (updateResult == true) {
            if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
                viewModel.loadVictimDetail(disasterId, victimId)
            }
            navController.currentBackStackEntry?.savedStateHandle?.remove<Boolean>("victim_updated")
        }
    }

    LaunchedEffect(disasterId, victimId) {
        if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
            viewModel.loadVictimDetail(disasterId, victimId)
        }
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            Toast.makeText(context, "Data korban berhasil dihapus", Toast.LENGTH_SHORT).show()
            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("victim_updated", true)
            navController.popBackStack()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                AppTopAppBar(
                    title = "Detail Korban",
                    canNavigateBack = true,
                    onNavigateUp = { navController.navigateUp() },
                    actions = {
                        IconButton(onClick = {
                            if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
                                viewModel.refreshVictimDetail(disasterId, victimId)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { navController.navigate("update-disaster-victim/$disasterId/$victimId") }) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Ubah", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { showDeleteVictimDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus Data", tint = MaterialTheme.colorScheme.error)
                        }
                    }
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
                    uiState.errorMessage != null && uiState.victim == null -> {
                        Text(text = uiState.errorMessage!!, modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
                    }
                    uiState.victim != null -> {
                        VictimDetailContent(
                            victim = uiState.victim!!,
                            onImageClick = { imageUrl -> selectedImageUrl = imageUrl },
                            onDeletePictureClick = { picture -> pictureToDelete = picture },
                            onAddPictures = { uris ->
                                uris.forEach { uri ->
                                    if (victimId != null) {
                                        viewModel.addPicture(victimId, uri, context)
                                    }
                                }
                            },
                            onRefresh = {
                                if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
                                    viewModel.refreshVictimDetail(disasterId, victimId)
                                }
                            }
                        )
                    }
                }
            }

            if (selectedImageUrl != null) {
                FullScreenImageViewer(imageUrl = selectedImageUrl!!, onDismiss = { selectedImageUrl = null })
            }

            if (showDeleteVictimDialog) {
                DeleteConfirmationDialog(
                    title = "Hapus Korban",
                    text = "Apakah Anda yakin ingin menghapus data korban ini? Tindakan ini tidak dapat dibatalkan.",
                    onConfirm = {
                        if (disasterId != null && victimId != null) {
                            viewModel.deleteVictim(disasterId, victimId)
                        }
                        showDeleteVictimDialog = false
                    },
                    onDismiss = { showDeleteVictimDialog = false }
                )
            }

            if (pictureToDelete != null) {
                DeleteConfirmationDialog(
                    title = "Hapus Gambar",
                    text = "Apakah Anda yakin ingin menghapus gambar ini?",
                    onConfirm = {
                        viewModel.deletePicture(pictureToDelete!!.id)
                        pictureToDelete = null
                    },
                    onDismiss = { pictureToDelete = null }
                )
            }

        }

        if (uiState.isLoading || uiState.isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = false) {},
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun VictimDetailContent(
    victim: DisasterVictim,
    onImageClick: (String) -> Unit,
    onDeletePictureClick: (VictimPicture) -> Unit,
    onAddPictures: (List<Uri>) -> Unit,
    onRefresh: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        VictimInfoCard(victim = victim)

        ImagePickerSection(
            uris = victim.pictures?.mapNotNull { picture ->
                // Use localPath if available (offline), otherwise use server URL
                when {
                    !picture.localPath.isNullOrEmpty() -> {
                        val file = java.io.File(picture.localPath)
                        if (file.exists()) {
                            Uri.fromFile(file)
                        } else {
                            // Local file doesn't exist, try server URL
                            if (!picture.url.isNullOrEmpty()) {
                                Uri.parse("https://e-disaster.fathur.tech${picture.url}")
                            } else null
                        }
                    }
                    !picture.url.isNullOrEmpty() -> Uri.parse("https://e-disaster.fathur.tech${picture.url}")
                    else -> null
                }
            } ?: emptyList(),
            onImagesAdded = onAddPictures,
            onImageRemoved = { uri ->
                val picture = victim.pictures?.find { pic ->
                    val picUri = when {
                        !pic.localPath.isNullOrEmpty() -> {
                            val file = java.io.File(pic.localPath)
                            if (file.exists()) Uri.fromFile(file) else null
                        }
                        !pic.url.isNullOrEmpty() -> Uri.parse("https://e-disaster.fathur.tech${pic.url}")
                        else -> null
                    }
                    picUri?.toString() == uri.toString()
                }
                picture?.let(onDeletePictureClick)
            }
        )
    }
}

@Composable
private fun VictimInfoCard(victim: DisasterVictim) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        if (dateString.isEmpty()) return "Tanggal tidak valid"
        return try {
            // Try to parse as timestamp (Long as string)
            val timestamp = dateString.toLongOrNull()
            if (timestamp != null) {
                val date = java.util.Date(timestamp)
                val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.forLanguageTag("id-ID"))
                return formatter.format(date)
            }
            
            // Try to parse as ISO date string
            try {
                val localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                return localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.forLanguageTag("id-ID")))
            } catch (e: Exception) {
                // Try other ISO formats
                try {
                    val localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
                    return localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", java.util.Locale.forLanguageTag("id-ID")))
                } catch (e2: Exception) {
                    // Try standard date formats
                    val formats = listOf(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "yyyy-MM-dd HH:mm:ss",
                        "yyyy-MM-dd"
                    )
                    for (format in formats) {
                        try {
                            val parsedDate = java.text.SimpleDateFormat(format, java.util.Locale.getDefault()).parse(dateString)
                            if (parsedDate != null) {
                                val formatter = java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.forLanguageTag("id-ID"))
                                return formatter.format(parsedDate)
                            }
                        } catch (e3: Exception) {
                            continue
                        }
                    }
                    // If all parsing fails, return original string or "Tanggal tidak valid"
                    if (dateString.length > 20) "Tanggal tidak valid" else dateString
                }
            }
        } catch (e: Exception) {
            "Tanggal tidak valid"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                victim.disasterTitle?.let { title ->
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = victim.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DisasterVictimStatusBadge(status = victim.status)
                DisasterEvacuationStatusBadge(isEvacuated = victim.isEvacuated)
            }

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "NIK", value = victim.nik)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(label = "Tanggal Lahir", value = formatDate(victim.dateOfBirth))
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(label = "Dilaporkan Oleh", value = victim.reporterName)
                }
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "Jenis Kelamin", value = victim.gender)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(label = "Kontak", value = victim.contactInfo)
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailItem(
                        label = "Tanggal Laporan",
                        value = formatDate(victim.createdAt)
                    )
                }
            }

            DetailItem(label = "Deskripsi", value = victim.description)
        }
    }
}

@Composable
private fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FullScreenImageViewer(imageUrl: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Gambar ukuran penuh",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentScale = ContentScale.Fit,
                placeholder = painterResource(id = R.drawable.app_logo),
                error = painterResource(id = R.drawable.app_logo)
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tutup",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun DeleteConfirmationDialog(title: String, text: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        containerColor = MaterialTheme.colorScheme.surface,
        confirmButton = { Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Ya, Hapus") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Preview(showBackground = true, name = "Detail Victim Light")
@Composable
fun DisasterVictimDetailScreenLightPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterVictimDetailScreen(navController = rememberNavController(), disasterId = "1", victimId = "1")
    }
}

@Preview(showBackground = true, name = "Detail Victim Dark")
@Composable
fun DisasterVictimDetailScreenDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterVictimDetailScreen(navController = rememberNavController(), disasterId = "1", victimId = "1")
    }
}

