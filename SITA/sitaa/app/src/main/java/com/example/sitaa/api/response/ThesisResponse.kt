package com.example.sitaa.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ThesisResponse(
    val success: Boolean,
    val message: String,
    val data: List<ThesisDetail>
) {
    data class ThesisDetail(
        val id: Int,
        val student: Student,
        val title: String,
        @SerializedName("research_object")
        val researchObject: String,
        val methodology: String,
        @SerializedName("attachment_file")
        val attachmentFile: String,
        val status: String,
        @SerializedName("rejection_reason")
        val rejectionReason: String?,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )

    data class Student(
        val id: Int,
        val name: String,
        val nim: String,
        @SerializedName("profile_photo")
        val profilePhoto: String?
    )
}