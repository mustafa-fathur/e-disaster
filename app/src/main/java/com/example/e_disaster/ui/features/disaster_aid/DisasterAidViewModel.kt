package com.example.e_disaster.ui.features.disaster_aid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.disaster_aid.DisasterAidListResponse
import com.example.e_disaster.data.repository.DisasterAidRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class DisasterAidUiState {
    object Idle : DisasterAidUiState()
    object Loading : DisasterAidUiState()
    data class Success(val data: DisasterAidListResponse) : DisasterAidUiState()
    data class Error(val message: String) : DisasterAidUiState()
}

@HiltViewModel
class DisasterAidViewModel @Inject constructor(
    private val repository: DisasterAidRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DisasterAidUiState>(DisasterAidUiState.Idle)
    val uiState: StateFlow<DisasterAidUiState> = _uiState.asStateFlow()

    private val _currentPage = MutableStateFlow(1)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    fun loadDisasterAids(
        disasterId: String,
        page: Int? = null,
        perPage: Int? = null,
        search: String? = null,
        category: String? = null
    ) {
        viewModelScope.launch {
            _uiState.value = DisasterAidUiState.Loading
            try {
                val response = repository.getDisasterAids(
                    disasterId = disasterId,
                    page = page ?: _currentPage.value,
                    perPage = perPage ?: 15,
                    search = search ?: _searchQuery.value,
                    category = category ?: _selectedCategory.value
                )
                _uiState.value = DisasterAidUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = DisasterAidUiState.Error(
                    e.message ?: "Gagal memuat data bantuan"
                )
            }
        }
    }


    fun setPage(page: Int) {
        _currentPage.value = page
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun resetFilters() {
        _currentPage.value = 1
        _searchQuery.value = null
        _selectedCategory.value = null
    }

    fun refresh(disasterId: String) {
        loadDisasterAids(disasterId)
    }
}
