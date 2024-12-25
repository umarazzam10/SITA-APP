package com.example.sitaa.screen.thesis.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.ThesisResponse.ThesisDetail
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class ThesisListViewModel : ViewModel() {
    var thesisList by mutableStateOf<List<ThesisDetail>>(emptyList())
    var filteredList by mutableStateOf<List<ThesisDetail>>(emptyList())
    var selectedStatus by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")

    private val apiService = RetrofitClient.createService()

    fun getThesisList(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getThesisList(
                    token = "Bearer $token",
                    status = selectedStatus,
                    search = if (searchQuery.isNotBlank()) searchQuery else null
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    thesisList = response.body()?.data ?: emptyList()
                    filterThesisList()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
        filterThesisList()
    }

    fun updateSelectedStatus(status: String?) {
        selectedStatus = status
        filterThesisList()
    }

    private fun filterThesisList() {
        filteredList = thesisList.filter { thesis ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                thesis.student.name.contains(searchQuery, ignoreCase = true) ||
                        thesis.student.nim.contains(searchQuery, ignoreCase = true) ||
                        thesis.title.contains(searchQuery, ignoreCase = true)
            }

            val matchesStatus = selectedStatus == null || thesis.status == selectedStatus

            matchesSearch && matchesStatus
        }
    }
}