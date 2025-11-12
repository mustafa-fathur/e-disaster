package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.e_disaster.ui.theme.EDisasterTheme
import com.example.e_disaster.utils.DummyData
import com.example.e_disaster.data.model.History
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDisasterReportScreen(navController: NavController, reportId: String?) {
    // Prefill from dummy data if available
    val existing = DummyData.getHistoryById(reportId)

    var title by remember { mutableStateOf(existing?.disasterName ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var isFinal by remember { mutableStateOf(existing?.status.equals("completed", ignoreCase = true)) }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Ubah Perkembangan Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            UpdateDisasterReportContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                isFinal = isFinal,
                onIsFinalChange = { isFinal = it },
                onSave = { newTitle, newDescription, imagePath, isFinalFlag ->
                    // Persist to DummyData for preview/testing
                    if (existing != null) {
                        val updated = existing.copy(
                            disasterName = newTitle,
                            description = newDescription,
                            imageUrl = imagePath ?: existing.imageUrl,
                            status = if (isFinalFlag) "completed" else existing.status
                        )
                        DummyData.updateHistory(updated)
                    } else {
                        val newHistory = History(
                            id = reportId ?: UUID.randomUUID().toString(),
                            disasterName = newTitle,
                            location = "",
                            date = "",
                            description = newDescription,
                            imageUrl = imagePath ?: "",
                            status = if (isFinalFlag) "completed" else "in_progress",
                            participants = emptyList()
                        )
                        DummyData.addHistory(newHistory)
                    }
                    navController.navigateUp()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDisasterReportContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    isFinal: Boolean,
    onIsFinalChange: (Boolean) -> Unit,
    onSave: (title: String, description: String, imagePath: String?, isFinal: Boolean) -> Unit = { _, _, _, _ -> }
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(text = "Judul Laporan") },
            leadingIcon = {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tt",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { onTitleChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Description
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = "Deskripsi") },
            leadingIcon = {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Tt",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            trailingIcon = {
                if (description.isNotEmpty()) {
                    IconButton(onClick = { onDescriptionChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Photo upload (placeholder)
        Text(text = "Foto Bencana", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF0F0F0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Foto", fontSize = 12.sp, color = Color.Gray)
            }

            TextButton(onClick = { /* TODO: open image picker */ }) {
                Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Upload Foto")
            }
        }

        // Final checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFinal, onCheckedChange = onIsFinalChange)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ini adalah laporan tahap akhir")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onSave(title, description, null, isFinal) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Simpan", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateDisasterReportPreview() {
    EDisasterTheme {
        UpdateDisasterReportContent(
            modifier = Modifier.padding(16.dp),
            title = "Erupsi Gunung",
            onTitleChange = {},
            description = "Deskripsi contoh",
            onDescriptionChange = {},
            isFinal = false,
            onIsFinalChange = {},
            onSave = { _, _, _, _ -> }
        )
    }
}
