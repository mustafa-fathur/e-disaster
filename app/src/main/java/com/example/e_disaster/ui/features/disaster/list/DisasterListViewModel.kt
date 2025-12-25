package com.example.e_disaster.ui.features.disaster.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.Disaster
import com.example.e_disaster.data.repository.DisasterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DisasterListViewModel @Inject constructor(
    private val disasterRepository: DisasterRepository
) : ViewModel() {

    var disasters by mutableStateOf<List<Disaster>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedStatus by mutableStateOf<String?>(null)
        private set

    var selectedType by mutableStateOf<String?>(null)
        private set

    private var searchJob: Job? = null

    init {
        fetchDisasters()
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500L)
            fetchDisasters()
        }
    }

    fun onStatusSelected(status: String?) {
        selectedStatus = status
        fetchDisasters()
    }

    fun onTypeSelected(type: String?) {
        selectedType = type
        fetchDisasters()
    }

    fun refresh() {
        fetchDisasters()
    }

    private fun fetchDisasters() {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                disasters = disasterRepository.getDisasters(
                    search = searchQuery.ifBlank { null },
                    status = selectedStatus,
                    type = selectedType
                )
            } catch (e: Exception) {
                errorMessage = "Gagal memuat data: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
