package com.example.sitaa.api.response

import com.example.sitaa.model.User
import com.google.gson.annotations.SerializedName

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
) {
    data class AuthData(
        val token: String,
        val user: User
    )
}