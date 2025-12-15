package com.example.e_disaster.ui.features.disaster_victim.detail

import android.os.Build
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.e_disaster.R
import com.example.e_disaster.data.model.DisasterVictim
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.ui.components.badges.DisasterEvacuationStatusBadge
import com.example.e_disaster.ui.components.badges.DisasterVictimStatusBadge
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

    var selectedImageUrl by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(disasterId, victimId) {
        Log.d("VictimDetailScreen", "LaunchedEffect triggered. DisasterID: '$disasterId', VictimID: '$victimId'")
        if (!disasterId.isNullOrEmpty() && !victimId.isNullOrEmpty()) {
            Log.d("VictimDetailScreen", "IDs are valid, calling loadVictimDetail.")
            viewModel.loadVictimDetail(disasterId, victimId)
        } else {
            Log.e("VictimDetailScreen", "Navigasi Gagal: Satu atau kedua ID null atau kosong.")
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Korban",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    IconButton(onClick = { navController.navigate("update-disaster-victim/$disasterId/$victimId") }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                uiState.victim != null -> {
                    VictimDetailContent(
                        victim = uiState.victim!!,
                        onImageClick = { imageUrl ->
                            selectedImageUrl = imageUrl
                        }
                    )
                }
            }
        }
        if (selectedImageUrl != null) {
            FullScreenImageViewer(
                imageUrl = selectedImageUrl!!,
                onDismiss = { selectedImageUrl = null }
            )
        }
    }
}

@Composable
private fun VictimInfoCard(victim: DisasterVictim) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatDate(dateString: String): String {
        return try {
            val localDate = try {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            } catch (e: Exception) {
                LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            }
            localDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        } catch (e: Exception) {
            dateString
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
private fun PhotoSection(
    pictures: List<VictimPicture>,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Foto Korban",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = "Foto Korban (${pictures.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pictures) { picture ->
                val fullImageUrl = "https://e-disaster.fathur.tech${picture.url}"
                Card(
                    modifier = Modifier
                        .size(140.dp, 120.dp)
                        .clickable { onImageClick(fullImageUrl) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AsyncImage(
                        model = fullImageUrl,
                        contentDescription = picture.caption ?: "Foto korban",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.placeholder),
                        error = painterResource(id = R.drawable.placeholder)
                    )
                }
            }
        }
    }
}

@Composable
private fun VictimDetailContent(
    victim: DisasterVictim,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        VictimInfoCard(victim = victim)
        victim.pictures?.takeIf { it.isNotEmpty() }?.let { pictures ->
            PhotoSection(
                pictures = pictures,
                onImageClick = onImageClick
            )
        }
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
                placeholder = painterResource(id = R.drawable.placeholder),
                error = painterResource(id = R.drawable.placeholder)
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
