@file:Suppress("DEPRECATION")

package com.example.e_disaster.ui.features.auth.register

import android.icu.text.SimpleDateFormat
import android.widget.Toast
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.e_disaster.R
import com.example.e_disaster.ui.components.AppDatePickerDialog
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is RegisterUiState.Success -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                }
                else -> { /* Handle other one-time events if needed */ }
            }
        }
    }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    var showDatePicker by remember { mutableStateOf(false) }

    fun onDateSelected(dateMillis: Long?) {
        dateMillis?.let {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
            viewModel.birthDate = simpleDateFormat.format(Date(it))
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
        },
        containerColor = MaterialTheme.colorScheme.surface
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

                OutlinedTextField(value = viewModel.name, onValueChange = { viewModel.name = it }, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.email, onValueChange = { viewModel.email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.nik, onValueChange = { viewModel.nik = it }, label = { Text("NIK") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(painterResource(id = R.drawable.id_card), contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.phone, onValueChange = { viewModel.phone = it }, label = { Text("Nomor Telepon") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.address, onValueChange = { viewModel.address = it }, label = { Text("Alamat") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) })

                Spacer(modifier = Modifier.height(16.dp))

                Text("Jenis Kelamin", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.fillMaxWidth())
                Row(Modifier.fillMaxWidth()) {
                    listOf("Laki-laki", "Perempuan").forEach { text ->
                        Row(
                            Modifier.selectable(
                                selected = (text == viewModel.selectedGender),
                                onClick = { viewModel.selectedGender = text }
                            ).padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == viewModel.selectedGender),
                                onClick = { viewModel.selectedGender = text }
                            )
                            Text(text = text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = viewModel.birthDate,
                    onValueChange = {},
                    label = { Text("Tanggal Lahir") },
                    modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Pilih tanggal") },
                    readOnly = true,
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                )

                if (showDatePicker) {
                    AppDatePickerDialog(
                        datePickerState = datePickerState,
                        onDismiss = { showDatePicker = false },
                        onConfirm = {
                            onDateSelected(datePickerState.selectedDateMillis)
                            showDatePicker = false
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.reasonToJoin, onValueChange = { viewModel.reasonToJoin = it }, label = { Text("Alasan Bergabung") }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(painterResource(id = R.drawable.text_fields), contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.password, onValueChange = { viewModel.password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) })
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.confirmPassword, onValueChange = { viewModel.confirmPassword = it }, label = { Text("Konfirmasi Password") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation(), leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) })

                Spacer(modifier = Modifier.height(8.dp))

                val currentState = viewModel.uiState
                if (currentState is RegisterUiState.Error) {
                    Text(
                        text = currentState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.onRegisterClicked() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = currentState !is RegisterUiState.Loading
                ) {
                    if (currentState is RegisterUiState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                    } else {
                        Text("Daftar", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "⚠\uFE0F Akun Anda akan ditinjau oleh admin sebelum diaktifkan",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
