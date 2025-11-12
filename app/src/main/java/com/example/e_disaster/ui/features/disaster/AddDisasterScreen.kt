package com.example.e_disaster.ui.features.disaster

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.e_disaster.ui.components.partials.AppTopAppBar
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDisasterScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedDisasterType by remember { mutableStateOf("Gempa Bumi") }
    val disasterTypes = listOf("Gempa Bumi", "Banjir", "Tsunami", "Gunung Meletus")

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth -> date = "$dayOfMonth/${month + 1}/$year" },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute -> time = String.format("%02d:%02d", hour, minute) },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> imageUri = uri }
    )

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = "Tambah Bencana",
                canNavigateBack = true,
                onNavigateUp = { navController.popBackStack() })
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item { OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Judul") }, leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) }, modifier = Modifier.fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) }, modifier = Modifier.fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                ExposedDropdownMenuBox(expanded = typeExpanded, onExpandedChange = { typeExpanded = !typeExpanded }) {
                    OutlinedTextField(
                        value = selectedDisasterType,
                        onValueChange = {},
                        label = { Text("Jenis Bencana") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                        disasterTypes.forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { selectedDisasterType = it; typeExpanded = false })
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokasi") }, leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null) }, modifier = Modifier.fillMaxWidth()) }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = latitude, onValueChange = { latitude = it }, label = { Text("Latitude") }, leadingIcon = { Icon(Icons.Default.GpsFixed, contentDescription = null) }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = longitude, onValueChange = { longitude = it }, label = { Text("Longitude") }, leadingIcon = { Icon(Icons.Default.GpsFixed, contentDescription = null) }, modifier = Modifier.weight(1f))
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(value = date, onValueChange = {}, label = { Text("Tanggal") }, leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }, readOnly = true, modifier = Modifier
                        .weight(1f)
                        .clickable { datePickerDialog.show() })
                    OutlinedTextField(value = time, onValueChange = {}, label = { Text("Waktu") }, leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) }, readOnly = true, modifier = Modifier
                        .weight(1f)
                        .clickable { timePickerDialog.show() })
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                OutlinedTextField(
                    value = imageUri?.path ?: "",
                    onValueChange = { },
                    label = { Text("Foto Bencana") },
                    leadingIcon = { Icon(Icons.Default.UploadFile, contentDescription = null) },
                    trailingIcon = { IconButton(onClick = { launcher.launch("image/*") }) { Icon(Icons.Default.AttachFile, contentDescription = null) } },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                Button(
                    onClick = { /*TODO*/ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Simpan", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDisasterScreenPreview() {
    EDisasterTheme {
        AddDisasterScreen(rememberNavController())
    }
}
