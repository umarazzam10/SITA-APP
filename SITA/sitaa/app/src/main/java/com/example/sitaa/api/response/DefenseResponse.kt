package com.example.sitaa.api.response

import com.google.gson.annotations.SerializedName
import java.util.Date

data class DefenseResponse(
    val success: Boolean,
    val message: String,
    val data: List<DefenseDetail>
) {
    data class DefenseDetail(
        val id: Int,
        val student: Student,
        @SerializedName("seminar_id")
        val seminarId: Int,
        @SerializedName("defense_date")
        val defenseDate: Date? = null,
        @SerializedName("approval_letter_file")
        val approvalLetterFile: String? = null,
        val status: String,
        @SerializedName("rejection_reason")
        val rejectionReason: String? = null,
        @SerializedName("suggested_date")
        val suggestedDate: Date? = null,
        @SerializedName("created_at")
        val createdAt: String? = null,
        @SerializedName("updated_at")
        val updatedAt: String? = null,
        val seminar: SeminarDetail? = null
    )

    data class Student(
        val id: Int,
        val name: String,
        val nim: String,
        @SerializedName("profile_photo")
        val profilePhoto: String?
    )

    data class SeminarDetail(
        val id: Int,
        val title: String,
        @SerializedName("seminar_date")
        val seminarDate: Date? = null,
        val thesis: ThesisDetail? = null
    )

    data class ThesisDetail(
        val id: Int,
        val title: String,
        @SerializedName("research_object")
        val researchObject: String,
        val methodology: String,
        @SerializedName("attachment_file")
        val attachmentFile: String
    )
}