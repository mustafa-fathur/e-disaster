package com.example.e_disaster.ui.features.disaster_aid

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Warning
import com.example.e_disaster.data.remote.dto.disaster_aid.CreateAidRequest
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterAidScreen(
    navController: NavController,
    disasterId: String?,
    viewModel: AddDisasterAidViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val lat by viewModel.lat.collectAsState()
    val long by viewModel.long.collectAsState()
    val locationLoading by viewModel.locationLoading.collectAsState()
    val locationError by viewModel.locationError.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    val categoryOptions = listOf(
        "makanan", "minuman", "obat", "pakaian", "tenda", "selimut", "alat_medis", "air_bersih", "lainnya"
    )
    var categoryExpanded by remember { mutableStateOf(false) }

    var quantityText by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    val unitOptions = listOf("paket", "buah", "kg", "liter", "dus")
    var unitExpanded by remember { mutableStateOf(false) }

    var donor by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }
    LaunchedEffect(disasterId) {
        disasterId?.let { viewModel.loadDisasterLocation(it) }
    }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AddAidUiState.Success -> {
                Toast.makeText(context, "Bantuan berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                navController.navigateUp()
            }
            is AddAidUiState.Error -> {
                val msg = (uiState as AddAidUiState.Error).message
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Bantuan Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        if (disasterId == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Disaster ID tidak tersedia.")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Judul Bantuan
            Text("Judul Bantuan", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contoh: Paket Sembako") },
                leadingIcon = { Icon(imageVector = Icons.Default.TextFields, contentDescription = null) },
                trailingIcon = {
                    if (name.isNotBlank()) {
                        androidx.compose.material3.IconButton(onClick = { name = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus")
                        }
                    }
                }
            )

            // Deskripsi
            Text("Deskripsi", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Jelaskan detail bantuna...") },
                leadingIcon = { Icon(imageVector = Icons.Default.TextFields, contentDescription = null) },
                trailingIcon = {
                    if (description.isNotBlank()) {
                        androidx.compose.material3.IconButton(onClick = { description = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus")
                        }
                    }
                }
            )

            // Kategori
            Text("Kategori", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(
                    value = if (category.isBlank()) "Pilih kategori" else category,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.TextFields, contentDescription = null) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    }
                )
                DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(text = { Text(option) }, onClick = {
                            category = option
                            categoryExpanded = false
                        })
                    }
                }
            }

            // Jumlah & Satuan
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Jumlah", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it.filter { ch -> ch.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("100") }
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Satuan", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                    ExposedDropdownMenuBox(expanded = unitExpanded, onExpandedChange = { unitExpanded = !unitExpanded }) {
                        OutlinedTextField(
                            value = if (unit.isBlank()) "Pilih" else unit,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded)
                            },
                            leadingIcon = { Icon(imageVector = Icons.Default.TextFields, contentDescription = null) }
                        )
                        DropdownMenu(expanded = unitExpanded, onDismissRequest = { unitExpanded = false }) {
                            unitOptions.forEach { u ->
                                DropdownMenuItem(text = { Text(u) }, onClick = {
                                    unit = u
                                    unitExpanded = false
                                })
                            }
                        }
                    }
                }
            }

            // Donatur
            Text("Donatur", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = donor,
                onValueChange = { donor = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contoh: Komensos RI") },
                leadingIcon = { Icon(imageVector = Icons.Default.Business, contentDescription = null) },
                trailingIcon = {
                    if (donor.isNotBlank()) {
                        androidx.compose.material3.IconButton(onClick = { donor = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus")
                        }
                    }
                }
            )

            // Lokasi Distribusi
            Text("Lokasi Distribusi", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Contoh: Posko Cianjur") },
                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (location.isNotBlank()) {
                        androidx.compose.material3.IconButton(onClick = { location = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Hapus")
                        }
                    }
                }
            )

            // Foto Bencana
            Text("Foto Bencana", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Button(onClick = { pickImageLauncher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (imageUri == null) "Upload Foto" else "Ganti Foto")
            }
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto Bencana",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }
            if (locationLoading) {
                Text("Memuat koordinat bencana...")
            } else if (locationError != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Koordinat bencana tidak bisa dimuat",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = locationError!!,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { disasterId?.let { viewModel.loadDisasterLocation(it) } },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.onErrorContainer,
                                contentColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text("Coba lagi")
                        }
                    }
                }
            } else {
                Text(
                    text = "Koordinat bencana: ${lat ?: "-"}, ${long ?: "-"}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val quantity = quantityText.toIntOrNull()
                    if (name.isBlank() || category.isBlank() || quantity == null || description.isBlank() || location.isBlank()) {
                        Toast.makeText(context, "Lengkapi semua field dengan benar", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    viewModel.submit(
                        disasterId = disasterId,
                        name = name,
                        category = category,
                        quantity = quantity,
                        description = description,
                        location = location
                    )
                },
                enabled = uiState !is AddAidUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.CenterHorizontally),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B35), // Orange primary
                    contentColor = Color.White
                )
            ) {
                if (uiState is AddAidUiState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Simpan", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                }
            }
        }
    }
}