package com.example.e_disaster.data.repository

import com.example.e_disaster.data.remote.dto.notification.NotificationDetailResponse
import com.example.e_disaster.data.remote.dto.notification.NotificationListResponse
import com.example.e_disaster.data.remote.dto.notification.NotificationStatsResponse
import com.example.e_disaster.data.remote.service.NotificationApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: NotificationApiService
) {

    suspend fun getNotifications(
        page: Int? = null,
        perPage: Int? = null,
        search: String? = null,
        category: String? = null,
        isRead: Boolean? = null
    ): NotificationListResponse {
        return apiService.getNotifications(
            page = page,
            perPage = perPage,
            search = search,
            category = category,
            isRead = isRead
        )
    }

    suspend fun getNotificationDetail(notificationId: String): NotificationDetailResponse {
        return apiService.getNotificationDetail(notificationId)
    }

    suspend fun markNotificationAsRead(notificationId: String): NotificationDetailResponse {
        return apiService.markNotificationAsRead(notificationId)
    }

    suspend fun markAllNotificationsAsRead(): String {
        val response = apiService.markAllNotificationsAsRead()
        return response.message
    }

    suspend fun deleteNotification(notificationId: String): String {
        val response = apiService.deleteNotification(notificationId)
        return response.message
    }

    suspend fun deleteAllReadNotifications(): String {
        val response = apiService.deleteAllReadNotifications()
        return response.message
    }

    suspend fun getNotificationStats(): NotificationStatsResponse {
        return apiService.getNotificationStats()
    }
}

