package com.example.sitaa.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Seminar(
    val id: Int,
    @SerializedName("thesis_id")
    val thesisId: Int,
    @SerializedName("student_id")
    val studentId: Int,
    val title: String,
    @SerializedName("seminar_date")
    val seminarDate: Date?,
    val status: String,
    @SerializedName("rejection_reason")
    val rejectionReason: String?,
    @SerializedName("suggested_date")
    val suggestedDate: Date?,
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
        val profilePhoto: String?
    )

    enum class Status {
        @SerializedName("pending")
        PENDING,
        @SerializedName("approved")
        APPROVED,
        @SerializedName("rejected")
        REJECTED
    }
}