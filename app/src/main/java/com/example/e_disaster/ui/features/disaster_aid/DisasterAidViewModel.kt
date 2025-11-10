package com.example.e_disaster.ui.features.disaster_aid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.model.AidStatus
import com.example.e_disaster.data.model.AidType
import com.example.e_disaster.data.model.CreateDisasterAidRequest
import com.example.e_disaster.data.model.DisasterAid
import com.example.e_disaster.data.model.UpdateDisasterAidRequest
import com.example.e_disaster.data.repository.DisasterAidRepository
import com.example.e_disaster.utils.DummyData
import com.example.e_disaster.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DisasterAidViewModel : ViewModel() {

    private val repository = DisasterAidRepository()

    // State untuk daftar bantuan bencana tertentu
    private val _disasterAids = MutableStateFlow<Resource<List<DisasterAid>>>(Resource.Loading())
    val disasterAids: StateFlow<Resource<List<DisasterAid>>> = _disasterAids.asStateFlow()

    // State untuk bantuan di sekitar lokasi
    private val _nearbyAids = MutableStateFlow<Resource<List<DisasterAid>>>(Resource.Loading())
    val nearbyAids: StateFlow<Resource<List<DisasterAid>>> = _nearbyAids.asStateFlow()

    // State untuk detail bantuan
    private val _selectedAid = MutableStateFlow<DisasterAid?>(null)
    val selectedAid: StateFlow<DisasterAid?> = _selectedAid.asStateFlow()

    // State untuk operasi create/update/delete
    private val _operationResult = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val operationResult: StateFlow<Resource<Unit>> = _operationResult.asStateFlow()

    // Filter states
    private val _selectedStatus = MutableStateFlow<AidStatus?>(null)
    val selectedStatus: StateFlow<AidStatus?> = _selectedStatus.asStateFlow()

    private val _selectedType = MutableStateFlow<AidType?>(null)
    val selectedType: StateFlow<AidType?> = _selectedType.asStateFlow()

    private val _radius = MutableStateFlow(10.0) // Default 10km
    val radius: StateFlow<Double> = _radius.asStateFlow()

    /**
     * Memuat daftar bantuan untuk bencana tertentu
     */
    fun loadDisasterAids(
        disasterId: String,
        userLatitude: Double? = null,
        userLongitude: Double? = null
    ) {
        viewModelScope.launch {
            // Untuk sementara gunakan data dummy, nanti diganti dengan API call
            try {
                _disasterAids.value = Resource.Loading()

                // Simulate API delay
                delay(500)

                // Filter data dummy berdasarkan disasterId (untuk demo)
                val filteredAids = DummyData.dummyDisasterAids.filter { aid ->
                    (_selectedStatus.value == null || aid.status == _selectedStatus.value?.value) &&
                    (_selectedType.value == null || aid.type == _selectedType.value?.value)
                }

                _disasterAids.value = Resource.Success(filteredAids)

            } catch (e: Exception) {
                _disasterAids.value = Resource.Error("Gagal memuat data: ${e.localizedMessage}")
            }

            // Uncomment below when API is ready
            /*
            repository.getDisasterAids(
                disasterId = disasterId,
                userLatitude = userLatitude,
                userLongitude = userLongitude,
                radius = if (userLatitude != null && userLongitude != null) _radius.value else null,
                status = _selectedStatus.value?.value,
                type = _selectedType.value?.value
            ).collect { resource ->
                _disasterAids.value = resource
            }
            */
        }
    }

    /**
     * Memuat daftar bantuan di sekitar lokasi pengguna
     */
    fun loadNearbyAids(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            repository.getNearbyAids(
                latitude = latitude,
                longitude = longitude,
                radius = _radius.value,
                status = _selectedStatus.value?.value,
                type = _selectedType.value?.value
            ).collect { resource ->
                _nearbyAids.value = resource
            }
        }
    }

    /**
     * Membuat bantuan baru
     */
    fun createDisasterAid(
        disasterId: String,
        name: String,
        type: String,
        quantity: Int,
        unit: String,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        description: String? = null,
        contactPerson: String? = null,
        contactPhone: String? = null,
        estimatedArrival: String? = null
    ) {
        viewModelScope.launch {
            val request = CreateDisasterAidRequest(
                name = name,
                type = type,
                quantity = quantity,
                unit = unit,
                location = location,
                latitude = latitude,
                longitude = longitude,
                description = description,
                contactPerson = contactPerson,
                contactPhone = contactPhone,
                estimatedArrival = estimatedArrival
            )

            repository.createDisasterAid(disasterId, request).collect { resource ->
                _operationResult.value = when (resource) {
                    is Resource.Success -> {
                        // Refresh data setelah berhasil create
                        loadDisasterAids(disasterId)
                        Resource.Success(Unit)
                    }
                    is Resource.Error -> resource as Resource<Unit>
                    is Resource.Loading -> Resource.Loading()
                }
            }
        }
    }

    /**
     * Update bantuan
     */
    fun updateDisasterAid(
        disasterId: String,
        aidId: String,
        name: String? = null,
        type: String? = null,
        quantity: Int? = null,
        unit: String? = null,
        status: String? = null,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        description: String? = null,
        contactPerson: String? = null,
        contactPhone: String? = null,
        estimatedArrival: String? = null
    ) {
        viewModelScope.launch {
            val request = UpdateDisasterAidRequest(
                name = name,
                type = type,
                quantity = quantity,
                unit = unit,
                status = status,
                location = location,
                latitude = latitude,
                longitude = longitude,
                description = description,
                contactPerson = contactPerson,
                contactPhone = contactPhone,
                estimatedArrival = estimatedArrival
            )

            repository.updateDisasterAid(disasterId, aidId, request).collect { resource ->
                _operationResult.value = when (resource) {
                    is Resource.Success -> {
                        // Refresh data setelah berhasil update
                        loadDisasterAids(disasterId)
                        Resource.Success(Unit)
                    }
                    is Resource.Error -> resource as Resource<Unit>
                    is Resource.Loading -> Resource.Loading()
                }
            }
        }
    }

    /**
     * Hapus bantuan
     */
    fun deleteDisasterAid(disasterId: String, aidId: String) {
        viewModelScope.launch {
            repository.deleteDisasterAid(disasterId, aidId).collect { resource ->
                _operationResult.value = when (resource) {
                    is Resource.Success -> {
                        // Refresh data setelah berhasil delete
                        loadDisasterAids(disasterId)
                        Resource.Success(Unit)
                    }
                    is Resource.Error -> resource as Resource<Unit>
                    is Resource.Loading -> Resource.Loading()
                }
            }
        }
    }

    /**
     * Set filter status
     */
    fun setStatusFilter(status: AidStatus?) {
        _selectedStatus.value = status
    }

    /**
     * Set filter tipe bantuan
     */
    fun setTypeFilter(type: AidType?) {
        _selectedType.value = type
    }

    /**
     * Set radius pencarian
     */
    fun setRadius(radius: Double) {
        _radius.value = radius
    }

    /**
     * Set bantuan yang dipilih
     */
    fun setSelectedAid(aid: DisasterAid?) {
        _selectedAid.value = aid
    }

    /**
     * Reset semua filter
     */
    fun resetFilters() {
        _selectedStatus.value = null
        _selectedType.value = null
        _radius.value = 10.0
    }

    /**
     * Refresh data dengan filter saat ini
     */
    fun refreshData(disasterId: String? = null, latitude: Double? = null, longitude: Double? = null) {
        if (disasterId != null) {
            loadDisasterAids(disasterId, latitude, longitude)
        } else if (latitude != null && longitude != null) {
            loadNearbyAids(latitude, longitude)
        }
    }
}