package com.example.sitaa.screen.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showSuccessAlert by mutableStateOf(false)
    var showErrorAlert by mutableStateOf(false)

    private val apiService = RetrofitClient.createService()

    fun login(
        sharedPref: SharedPref,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null
                showSuccessAlert = false
                showErrorAlert = false

                val response = apiService.login(
                    mapOf(
                        "username" to username,
                        "password" to password
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { authData ->
                        // Save auth data
                        sharedPref.saveAuthToken(authData.token)
                        sharedPref.saveUserRole(authData.user.role)
                        sharedPref.saveUserId(authData.user.id)

                        showSuccessAlert = true
                        delay(2000) // Show success message for 2 seconds
                        onSuccess()
                    }
                } else {
                    errorMessage = response.body()?.message ?: "Login gagal"
                    showErrorAlert = true
                }
            } catch (e: HttpException) {
                errorMessage = "Error jaringan: ${e.message}"
                showErrorAlert = true
                Log.e("LoginViewModel", "HTTP Exception: ${e.message}")
            } catch (e: IOException) {
                errorMessage = "Tidak dapat terhubung ke server"
                showErrorAlert = true
                Log.e("LoginViewModel", "IO Exception: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun updateUsername(value: String) {
        username = value
        errorMessage = null
        showErrorAlert = false
    }

    fun updatePassword(value: String) {
        password = value
        errorMessage = null
        showErrorAlert = false
    }

    fun dismissAlerts() {
        showSuccessAlert = false
        showErrorAlert = false
    }
}