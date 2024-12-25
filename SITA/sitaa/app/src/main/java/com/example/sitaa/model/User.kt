package com.example.sitaa.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val role: String,
    val name: String,
    val nim: String? = null,
    @SerializedName("profile_photo")
    val profilePhoto: String? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)