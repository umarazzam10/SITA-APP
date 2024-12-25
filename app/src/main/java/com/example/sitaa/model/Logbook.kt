package com.example.sitaa.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Logbook(
    val id: Int,
    @SerializedName("student_id")
    val studentId: Int,
    val date: Date,
    val activity: String,
    val progress: String,
    @SerializedName("is_locked")
    val isLocked: Boolean,
    @SerializedName("lecturer_notes")
    val lecturerNotes: String?,
    @SerializedName("created_at")
    val createdAt: Date,
    @SerializedName("updated_at")
    val updatedAt: Date,
    val student: Student
) {
    data class Student(
        val id: Int,
        val name: String,
        val nim: String,
        @SerializedName("profile_photo")
        val profilePhoto: String?,
        @SerializedName("total_entries")
        val totalEntries: Int,
        @SerializedName("last_update")
        val lastUpdate: Date?
    )
}