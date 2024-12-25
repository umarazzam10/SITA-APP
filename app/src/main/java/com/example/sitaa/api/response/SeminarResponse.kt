package com.example.sitaa.api.response

import com.example.sitaa.model.User
import com.google.gson.annotations.SerializedName
import java.util.Date

data class SeminarResponse(
    val success: Boolean,
    val message: String,
    val data: List<SeminarDetail>
) {
    data class SeminarDetail(
        val id: Int,
        val student: Student,
        @SerializedName("thesis_id")
        val thesisId: Int,
        val title: String,
        @SerializedName("seminar_date")
        val seminarDate: Date? = null,
        val status: String,
        @SerializedName("rejection_reason")
        val rejectionReason: String? = null,
        @SerializedName("suggested_date")
        val suggestedDate: Date? = null,
        @SerializedName("created_at")
        val createdAt: String? = null,
        @SerializedName("updated_at")
        val updatedAt: String? = null
    )

    data class Student(
        val id: Int,
        val name: String,
        val nim: String,
        @SerializedName("profile_photo")
        val profilePhoto: String?
    )
}