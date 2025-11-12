@file:Suppress("DEPRECATION")

package com.example.e_disaster.ui.features.auth

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.AppDatePickerDialog
import com.example.e_disaster.ui.theme.EDisasterTheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var nik by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val genderOptions = listOf("Laki-laki", "Perempuan")
    var selectedGender by remember { mutableStateOf(genderOptions[0]) }
    var reason by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var showDatePicker by remember { mutableStateOf(false) }
    var birthDate by remember { mutableStateOf("") }

    fun onDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            birthDate = simpleDateFormat.format(Date(it))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(
                        id = if (isSystemInDarkTheme()) {
                            R.drawable.dark_app_logo
                        } else {
                            R.drawable.app_logo
                        }
                    ),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(100.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "e-Disaster",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Daftar Relawan",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Default
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form Fields
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = nik, onValueChange = { nik = it }, label = { Text("NIK") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(painterResource(id = R.drawable.id_card), contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Nomor Telepon") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) })

                Spacer(modifier = Modifier.height(16.dp))

                // Gender Selection
                Text("Jenis Kelamin", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth()) {
                    genderOptions.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedGender),
                                    onClick = { selectedGender = text }
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedGender),
                                onClick = { selectedGender = text }
                            )
                            Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    label = { Text("Tanggal Lahir") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Pilih tanggal")
                        }
                    },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
                    )
                )

                if (showDatePicker) {
                    AppDatePickerDialog(
                        datePickerState = datePickerState,
                        onDismiss = {showDatePicker = false},
                        onConfirm = {
                            onDateSelected(datePickerState.selectedDateMillis)
                            showDatePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("Alasan Bergabung") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(painterResource(id = R.drawable.text_fields), contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Konfirmasi Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) })

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { /* TODO: Register logic */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Daftar", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "⚠\uFE0F Akun Anda akan ditinjau oleh admin sebelum diaktifkan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

            }
        }
    }
}

@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun RegisterScreenLight() {
    EDisasterTheme(darkTheme = false) {
        RegisterScreen(navController = NavController(LocalContext.current))
    }
}

@Preview(showBackground = true, name = "Dark Mode")
@Composable
private fun RegisterScreenDark() {
    EDisasterTheme(darkTheme = true) {
        RegisterScreen(navController = NavController(LocalContext.current))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Date Picker - Light Mode")
@Composable
private fun DatePickerPreviewLight() {
    EDisasterTheme(darkTheme = false) {
        // The DatePicker is the component inside the dialog
        DatePicker(state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis()))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Date Picker - Dark Mode")
@Composable
private fun DatePickerPreviewDark() {
    EDisasterTheme(darkTheme = true) {
        DatePicker(state = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis()))
    }
}