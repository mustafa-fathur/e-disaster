package com.example.e_disaster.ui.features.disaster.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.local.database.dao.DisasterDao
import com.example.e_disaster.data.mapper.DisasterMapper
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisasterListViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository,
    private val disasterDao: DisasterDao
) : ViewModel() {

    // Reactive: Observe Room Flow for automatic updates
    val disasters: StateFlow<List<Disaster>> = disasterDao.getAllDisasters()
        .map { entities ->
            entities.map { entity ->
                with(DisasterMapper) { entity.toModel() }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        fetchDisasters()
    }

    // Initial fetch to populate database
    private fun fetchDisasters() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                // This will fetch from API if needed and save to Room
                // The Flow above will automatically update when Room changes
                disasterRepository.getDisasters()
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * Force refresh disasters from API
     */
    fun refreshDisasters() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                disasterRepository.refreshDisasters()
                // Flow will automatically update when Room changes
            } catch (e: Exception) {
                errorMessage = "Gagal refresh data: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
