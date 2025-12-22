package com.example.e_disaster.ui.features.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.dashboard.DashboardResponse
import com.example.e_disaster.data.remote.dto.dashboard.DashboardStats
import com.example.e_disaster.data.remote.dto.dashboard.RecentDisaster
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository
) : ViewModel() {

    var stats by mutableStateOf<DashboardStats?>(null)
        private set

    var recentDisasters by mutableStateOf<List<RecentDisaster>>(emptyList())
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
                recentDisasters = response.recentDisasters ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat dashboard: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
