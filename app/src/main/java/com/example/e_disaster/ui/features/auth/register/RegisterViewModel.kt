package com.example.e_disaster.ui.features.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.auth.RegisterRequest
import com.example.e_disaster.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val message: String) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var nik by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var selectedGender by mutableStateOf("Laki-laki")
    var birthDate by mutableStateOf("")
    var reasonToJoin by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")

    var uiState by mutableStateOf<RegisterUiState>(RegisterUiState.Idle)
        private set

    private val _eventFlow = MutableSharedFlow<RegisterUiState>()
    val eventFlow = _eventFlow.asSharedFlow()


    fun onRegisterClicked() {
        if (password != confirmPassword) {
            uiState = RegisterUiState.Error("Password dan konfirmasi password tidak cocok.")
            return
        }

        viewModelScope.launch {
            uiState = RegisterUiState.Loading
            try {
                val request = RegisterRequest(
                    name = name,
                    email = email,
                    password = password,
                    passwordConfirmation = confirmPassword,
                    nik = nik,
                    phone = phone,
                    address = address,
                    gender = if (selectedGender == "Laki-laki") 0 else 1,
                    dateOfBirth = formatDateForApi(birthDate),
                    reasonToJoin = reasonToJoin
                )

                val response = authRepository.register(request)
                _eventFlow.emit(RegisterUiState.Success(response.message))

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = "Registrasi gagal: ${e.message()} - ${errorBody ?: "No details"}"
                uiState = RegisterUiState.Error(errorMessage)
            } catch (e: Exception) {
                uiState = RegisterUiState.Error("Terjadi kesalahan: ${e.message}")
            } finally {
                if(uiState !is RegisterUiState.Error){
                    uiState = RegisterUiState.Idle
                }
            }
        }
    }

    private fun formatDateForApi(dateStr: String): String {
        if (dateStr.isBlank()) return ""
        return try {
            val parser = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            formatter.format(parser.parse(dateStr)!!)
        } catch (e: Exception) {
            ""
        }
    }
}
