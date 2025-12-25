package com.example.e_disaster.ui.features.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_disaster.data.remote.dto.notification.NotificationDto
import com.example.e_disaster.data.remote.dto.notification.NotificationStatsDto
import com.example.e_disaster.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val notifications: List<NotificationDto> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 1,
    val hasMorePages: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    private val _stats = MutableStateFlow<NotificationStatsDto?>(null)
    val stats: StateFlow<NotificationStatsDto?> = _stats.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _showUnreadOnly = MutableStateFlow(false)
    val showUnreadOnly: StateFlow<Boolean> = _showUnreadOnly.asStateFlow()

    private val _searchQuery = MutableStateFlow<String?>(null)
    val searchQuery: StateFlow<String?> = _searchQuery.asStateFlow()

    init {
        loadNotifications()
        loadStats()
    }

    fun loadNotifications(
        page: Int = 1,
        category: String? = null,
        isRead: Boolean? = null,
        search: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val response = repository.getNotifications(
                    page = page,
                    perPage = 15,
                    category = category ?: _selectedCategory.value,
                    isRead = isRead ?: if (_showUnreadOnly.value) false else null,
                    search = search ?: _searchQuery.value
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        notifications = response.data ?: emptyList(),
                        currentPage = response.pagination?.currentPage ?: 1,
                        totalPages = response.pagination?.lastPage ?: 1,
                        hasMorePages = (response.pagination?.currentPage ?: 1) < (response.pagination?.lastPage ?: 1)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal memuat notifikasi"
                    )
                }
                e.printStackTrace()
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            try {
                val response = repository.getNotificationStats()
                _stats.value = response.data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
        loadNotifications(category = category)
    }

    fun setShowUnreadOnly(showUnread: Boolean) {
        _showUnreadOnly.value = showUnread
        loadNotifications(isRead = if (showUnread) false else null)
    }

    fun setSearchQuery(query: String?) {
        _searchQuery.value = query
        loadNotifications(search = query)
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.markNotificationAsRead(notificationId)
                loadNotifications()
                loadStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                repository.markAllNotificationsAsRead()
                loadNotifications()
                loadStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notificationId)
                loadNotifications()
                loadStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAllRead() {
        viewModelScope.launch {
            try {
                repository.deleteAllReadNotifications()
                loadNotifications()
                loadStats()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refresh() {
        loadNotifications()
        loadStats()
    }
}

