package com.example.e_disaster.data.remote.service

import com.example.e_disaster.data.remote.dto.general.MessageResponse
import com.example.e_disaster.data.remote.dto.notification.NotificationDetailResponse
import com.example.e_disaster.data.remote.dto.notification.NotificationListResponse
import com.example.e_disaster.data.remote.dto.notification.NotificationStatsResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationApiService {

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int? = null,
        @Query("per_page") perPage: Int? = null,
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("is_read") isRead: Boolean? = null
    ): NotificationListResponse

    @GET("notifications/{id}")
    suspend fun getNotificationDetail(
        @Path("id") notificationId: String
    ): NotificationDetailResponse

    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Path("id") notificationId: String
    ): NotificationDetailResponse

    @PUT("notifications/read-all")
    suspend fun markAllNotificationsAsRead(): MessageResponse

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(
        @Path("id") notificationId: String
    ): MessageResponse

    @DELETE("notifications/read-all")
    suspend fun deleteAllReadNotifications(): MessageResponse

    @GET("notifications/stats")
    suspend fun getNotificationStats(): NotificationStatsResponse
}

