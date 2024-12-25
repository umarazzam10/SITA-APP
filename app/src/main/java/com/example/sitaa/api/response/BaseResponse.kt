package com.example.sitaa.api.response

data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<ErrorResponse>? = null
)

data class ErrorResponse(
    val field: String,
    val message: String
)

