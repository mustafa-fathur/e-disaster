package com.example.e_disaster.ui.features.disaster_aid.detail

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.R
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.data.model.VictimPicture
import com.example.e_disaster.ui.components.badges.DisasterAidCategoryBadge
import com.example.e_disaster.ui.components.form.ImagePickerSection
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.utils.Constants.BASE_URL
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DisasterAidDetailScreen(
    navController: NavController,
    disasterId: String?,
    aidId: String?,
    viewModel: DisasterAidDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(disasterId, aidId) {
        if (!disasterId.isNullOrEmpty() && !aidId.isNullOrEmpty()) {
            viewModel.loadAidDetail(disasterId, aidId)
        } else {
            Toast.makeText(context, "ID tidak valid", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Detail Bantuan",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() },
                actions = {
                    if (uiState is DisasterAidDetailUiState.Success) {
                        IconButton(onClick = { 
                            navController.navigate("update-disaster-aid/$disasterId/$aidId")
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Ubah",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
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
            when (val state = uiState) {
                is DisasterAidDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DisasterAidDetailUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        AidInfoCard(aid = state.aid)
                        
                        ImagePickerSection(
                            uris = state.aid.pictures?.map { "$BASE_URL${it.url}".toUri() } ?: emptyList(),
                            onImagesAdded = { uris ->
                                uris.forEach { uri ->
                                    if (disasterId != null && aidId != null) {
                                        viewModel.addPicture(disasterId, aidId, uri, context)
                                    }
                                }
                            },
                            onImageRemoved = { uri ->
                                val picture = state.aid.pictures?.find { 
                                    "$BASE_URL${it.url}" == uri.toString() 
                                }
                                if (picture != null && disasterId != null && aidId != null) {
                                    viewModel.deletePicture(disasterId, aidId, picture.id)
                                }
                            }
                        )
                    }
                }
                is DisasterAidDetailUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun AidInfoCard(aid: DisasterAid) {
    val formattedDate = try {
        val zonedDateTime = ZonedDateTime.parse(aid.createdAt)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        aid.createdAt
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = aid.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    DisasterAidCategoryBadge(category = aid.category)
                }
                Icon(
                    painter = painterResource(id = R.drawable.package_box),
                    contentDescription = "Aid Icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = aid.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "Jumlah", value = "${aid.quantity} ${aid.unit}")
                }
                Column(modifier = Modifier.weight(1f)) {
                    DetailItem(label = "Kategori", value = aid.category.replaceFirstChar { it.uppercase() })
                }
            }

            DetailItemWithIcon(
                icon = Icons.Default.CalendarMonth, 
                label = "Tanggal", 
                value = formattedDate
            )
            DetailItemWithIcon(
                icon = Icons.Default.Star, 
                label = "Donatur", 
                value = aid.donator.ifEmpty { "Tidak diketahui" }
            )
            DetailItemWithIcon(
                icon = Icons.Default.LocationOn, 
                label = "Lokasi", 
                value = aid.location.ifEmpty { "Tidak diketahui" }
            )
            if (!aid.reporterName.isNullOrEmpty()) {
                DetailItemWithIcon(
                    icon = Icons.Default.Person, 
                    label = "Dilaporkan oleh", 
                    value = aid.reporterName
                )
            }
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
private fun DetailItemWithIcon(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        DetailItem(label = label, value = value)
    }
}



@Preview(showBackground = true, name = "Detail Aid Light")
@Composable
fun DisasterAidDetailScreenLightPreview() {
    EDisasterTheme(darkTheme = false) {
        DisasterAidDetailScreen(
            navController = rememberNavController(), 
            disasterId = "123",
            aidId = "a1"
        )
    }
}

@Preview(showBackground = true, name = "Detail Aid Dark")
@Composable
fun DisasterAidDetailScreenDarkPreview() {
    EDisasterTheme(darkTheme = true) {
        DisasterAidDetailScreen(
            navController = rememberNavController(), 
            disasterId = "123",
            aidId = "a1"
        )
    }
}

