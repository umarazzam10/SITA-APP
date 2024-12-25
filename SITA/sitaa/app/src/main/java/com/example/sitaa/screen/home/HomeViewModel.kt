package com.example.sitaa.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.model.User
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var currentUser by mutableStateOf<User?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

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
                    currentUser = response.body()?.data
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
}