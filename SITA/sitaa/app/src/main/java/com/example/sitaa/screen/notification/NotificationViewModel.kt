package com.example.sitaa.screen.notification

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.NotificationResponse.NotificationDetail
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {
    private var _notifications by mutableStateOf<List<NotificationDetail>>(emptyList())
    var notifications: List<NotificationDetail>
        get() = _notifications
        private set(value) { _notifications = value }

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val apiService = RetrofitClient.createService()

    fun getNotifications(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getNotifications("Bearer $token")

                if (response.isSuccessful && response.body()?.success == true) {
                    _notifications = response.body()?.data ?: emptyList()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat notifikasi"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun markAsRead(notificationId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.markNotificationAsRead(
                    token = "Bearer $token",
                    notificationId = notificationId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    _notifications = _notifications.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(isRead = true)
                        } else {
                            notification
                        }
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun markAllAsRead(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.markAllNotificationsAsRead("Bearer $token")

                if (response.isSuccessful && response.body()?.success == true) {
                    _notifications = _notifications.map { it.copy(isRead = true) }
                }
            } catch (e: Exception) {
                errorMessage = e.message
            }
        }
    }

    fun refreshNotifications(sharedPref: SharedPref) {
        getNotifications(sharedPref)
    }
}