package com.example.e_disaster.ui.features.disaster_report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.e_disaster.ui.theme.EDisasterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterReportScreen(navController: NavController, disasterId: String?) {
    // Move state declarations here (like AddDisasterScreen)
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var long by remember { mutableStateOf("") }
    var isFinal by remember { mutableStateOf(false) }
    // disasterId is kept in the signature for future use

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Perkembangan Bencana",
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
            AddDisasterReportContent(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                title = title,
                onTitleChange = { title = it },
                description = description,
                onDescriptionChange = { description = it },
                lat = lat,
                onLatChange = { lat = it },
                long = long,
                onLongChange = { long = it },
                isFinal = isFinal,
                onIsFinalChange = { isFinal = it },
                onSave = { _, _, _, _, _ ->
                    // navigate back after saving
                    navController.navigateUp()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterReportContent(
    modifier: Modifier = Modifier,
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    lat: String,
    onLatChange: (String) -> Unit,
    long: String,
    onLongChange: (String) -> Unit,
    isFinal: Boolean,
    onIsFinalChange: (Boolean) -> Unit,
    onSave: (title: String, description: String, lat: String, long: String, isFinal: Boolean) -> Unit = { _, _, _, _, _ -> }
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Judul
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
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            trailingIcon = {
                if (title.isNotEmpty()) {
                    IconButton(onClick = { onTitleChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

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
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            trailingIcon = {
                if (description.isNotEmpty()) {
                    IconButton(onClick = { onDescriptionChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            }
        )

        // Lat / Long row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = lat,
                onValueChange = onLatChange,
                label = { Text(text = "Latitude", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                placeholder = { Text(text = "Lat") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (lat.isNotEmpty()) {
                        IconButton(onClick = { onLatChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            OutlinedTextField(
                value = long,
                onValueChange = onLongChange,
                label = { Text(text = "Longitude", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                placeholder = { Text(text = "Long") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                leadingIcon = { Icon(imageVector = Icons.Default.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (long.isNotEmpty()) {
                        IconButton(onClick = { onLongChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        // Upload photo
        Text(text = "Foto Bencana", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // simple placeholder box for image preview
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

        // Final report checkbox
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isFinal, onCheckedChange = onIsFinalChange)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Ini adalah laporan tahap akhir")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save button (prominent)
        Button(
            onClick = { onSave(title, description, lat, long, isFinal) },
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
fun AddDisasterReportPreview() {
    EDisasterTheme {
        // Provide remembered state holders in preview so the composable signature is satisfied
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var lat by remember { mutableStateOf("") }
        var long by remember { mutableStateOf("") }
        var isFinal by remember { mutableStateOf(false) }

        AddDisasterReportContent(
            modifier = Modifier.padding(16.dp),
            title = title,
            onTitleChange = {},
            description = description,
            onDescriptionChange = {},
            lat = lat,
            onLatChange = {},
            long = long,
            onLongChange = {},
            isFinal = isFinal,
            onIsFinalChange = {},
            onSave = { _, _, _, _, _ -> }
        )
    }
}
