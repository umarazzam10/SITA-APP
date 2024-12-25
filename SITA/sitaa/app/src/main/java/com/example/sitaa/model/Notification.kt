package com.example.sitaa.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Notification(
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
) {
    enum class TimeGroup {
        @SerializedName("today")
        TODAY,
        @SerializedName("this_week")
        THIS_WEEK,
        @SerializedName("this_month")
        THIS_MONTH,
        @SerializedName("older")
        OLDER
    }
}