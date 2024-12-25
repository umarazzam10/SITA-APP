package com.example.sitaa.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Thesis(
    val id: Int,
    @SerializedName("student_id")
    val studentId: Int,
    val title: String,
    @SerializedName("research_object")
    val researchObject: String,
    val methodology: String,
    @SerializedName("attachment_file")
    val attachmentFile: String,
    val status: String,
    @SerializedName("rejection_reason")
    val rejectionReason: String? = null,
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
        PENDING,
        APPROVED,
        REJECTED
    }
}