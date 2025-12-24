package com.example.e_disaster.ui.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.dashboard.DashboardStatsDto
import com.example.e_disaster.data.remote.dto.disaster.DisasterDto
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository
) : ViewModel() {

    var stats by mutableStateOf<DashboardStatsDto?>(null)
        private set

    var recentDisasters by mutableStateOf<List<DisasterDto>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchDashboard()
    }

    fun fetchDashboard() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val response = disasterRepository.getDashboard()
                stats = response.stats
                
                // Konversi dari Map ke List
                val disastersMap = response.recentDisasters
                recentDisasters = disastersMap?.values?.toList() ?: emptyList()
                
            } catch (e: Exception) {
                errorMessage = "Gagal memuat dashboard: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
