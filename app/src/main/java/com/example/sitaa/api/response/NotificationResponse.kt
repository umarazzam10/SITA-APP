package com.example.sitaa.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class NotificationResponse(
    val success: Boolean,
    val message: String,
    val data: List<NotificationDetail>
) {
    data class NotificationDetail(
        val id: Int,
        val title: String,
        val message: String,
        @SerializedName("is_read")
        val isRead: Boolean,
        @SerializedName("time_group")
        val timeGroup: String,
        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date
    )
}