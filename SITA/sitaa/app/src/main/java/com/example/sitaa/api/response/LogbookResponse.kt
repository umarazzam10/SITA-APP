package com.example.sitaa.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class LogbookResponse(
    val success: Boolean,
    val message: String,
    val data: LogbookData
) {
    data class LogbookData(
        val student: Student,
        val logbook: LogbookDetail,
        val pagination: Pagination? = null  // Make nullable
    )

    data class LogbookDetail(
        val entries: List<Entry>,
        @SerializedName("is_locked")
        val isLocked: Boolean
    )

    data class Entry(
        val id: Int,
        val date: Date,
        val activity: String,
        val progress: String,
        @SerializedName("lecturer_notes")
        val lecturerNotes: String?,
        @SerializedName("created_at")
        val createdAt: Date,
        @SerializedName("updated_at")
        val updatedAt: Date
    )

    data class Student(
        val id: Int,
        val name: String,
        val nim: String,
        @SerializedName("profile_photo")
        val profilePhoto: String?,
        @SerializedName("total_entries")
        val totalEntries: Int = 0,
        @SerializedName("last_update")
        val lastUpdate: Date? = null
    )

    data class Pagination(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("total_pages")
        val totalPages: Int,
        @SerializedName("total_items")
        val totalItems: Int,
        @SerializedName("has_next")
        val hasNext: Boolean,
        @SerializedName("has_prev")
        val hasPrev: Boolean
    )
}