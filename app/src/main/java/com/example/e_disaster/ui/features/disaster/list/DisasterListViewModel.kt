package com.example.e_disaster.ui.features.disaster.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisasterListViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository
) : ViewModel() {

    // Renamed for clarity
    var disasters by mutableStateOf<List<Disaster>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchDisasters()
    }

    // Renamed for clarity
    private fun fetchDisasters() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                // The repository now returns a fully mapped List<Disaster>
                disasters = disasterRepository.getDisasters()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
