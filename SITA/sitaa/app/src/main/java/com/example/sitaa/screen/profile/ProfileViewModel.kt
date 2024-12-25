package com.example.sitaa.screen.profile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.model.User
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class ProfileViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showEditDialog by mutableStateOf(false)
    var newUsername by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var showPasswordDialog by mutableStateOf(false)

    private val apiService = RetrofitClient.createService()

    fun getCurrentUser(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getCurrentUser("Bearer $token")
                if (response.isSuccessful && response.body()?.success == true) {
                    user = response.body()?.data
                    newUsername = user?.username ?: ""
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    private fun sanitizeInput(input: String): String {
        return input.trim().replace("\"", "").replace("'", "")
    }

    private fun validateInput(username: String, password: String): String? {
        if (username.isEmpty() && password.isEmpty()) {
            return "Username atau password harus diisi"
        }
        if (username.length < 3 && username.isNotEmpty()) {
            return "Username minimal 3 karakter"
        }
        if (password.length < 6 && password.isNotEmpty()) {
            return "Password minimal 6 karakter"
        }
        return null
    }

    fun updateProfile(
        sharedPref: SharedPref,
        photoUri: Uri? = null,
        photoFile: File? = null,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Validasi input
                val cleanUsername = sanitizeInput(newUsername)
                val cleanPassword = sanitizeInput(newPassword)

                val validationError = validateInput(cleanUsername, cleanPassword)
                if (validationError != null) {
                    errorMessage = validationError
                    Toast.makeText(context, validationError, Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Periksa apakah ada perubahan
                if (cleanUsername == user?.username && cleanPassword.isEmpty() && photoFile == null) {
                    errorMessage = "Tidak ada perubahan yang dilakukan"
                    Toast.makeText(context, "Tidak ada perubahan yang dilakukan", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken() ?: run {
                    errorMessage = "Session telah berakhir"
                    Toast.makeText(context, "Session telah berakhir", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Prepare profile photo if exists
                val photoPart = if (photoFile != null && photoUri != null) {
                    MultipartBody.Part.createFormData(
                        "profile_photo",
                        photoFile.name,
                        photoFile.asRequestBody("image/*".toMediaTypeOrNull())
                    )
                } else null

                val response = apiService.updateProfile(
                    token = "Bearer $token",
                    username = if (cleanUsername != user?.username) cleanUsername else null,
                    password = if (cleanPassword.isNotBlank()) cleanPassword else null,
                    profilePhoto = photoPart
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    user = response.body()?.data
                    clearDialogs()
                    Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memperbarui profil"
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    private fun clearDialogs() {
        showEditDialog = false
        showPasswordDialog = false
        newUsername = user?.username ?: ""
        newPassword = ""
    }

    fun logout(sharedPref: SharedPref, onLogout: () -> Unit) {
        viewModelScope.launch {
            sharedPref.clearSession()
            clearData()
            onLogout()
        }
    }

    private fun clearData() {
        user = null
        errorMessage = null
        newUsername = ""
        newPassword = ""
        showEditDialog = false
        showPasswordDialog = false
    }
}