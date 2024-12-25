package com.example.sitaa.screen.seminar.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.SeminarResponse.SeminarDetail
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class SeminarListViewModel : ViewModel() {
    var seminarList by mutableStateOf<List<SeminarDetail>>(emptyList())
    var filteredList by mutableStateOf<List<SeminarDetail>>(emptyList())
    var selectedStatus by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")

    private val apiService = RetrofitClient.createService()

    fun getSeminarList(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getSeminarList(
                    token = "Bearer $token",
                    status = selectedStatus,
                    search = if (searchQuery.isNotBlank()) searchQuery else null
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    // Transform data with proper photo URLs
                    seminarList = response.body()?.data?.map { seminar ->
                        seminar.copy(
                            student = seminar.student.copy(
                                profilePhoto = seminar.student.profilePhoto?.let {
                                    RetrofitClient.getFullFileUrl(it)
                                }
                            )
                        )
                    } ?: emptyList()
                    filterSeminarList()
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
        filterSeminarList()
    }

    fun updateSelectedStatus(status: String?, sharedPref: SharedPref) {
        selectedStatus = status
        getSeminarList(sharedPref)
    }

    private fun filterSeminarList() {
        filteredList = seminarList.filter { seminar ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                seminar.student.name.contains(searchQuery, ignoreCase = true) ||
                        seminar.student.nim.contains(searchQuery, ignoreCase = true) ||
                        seminar.title.contains(searchQuery, ignoreCase = true)
            }

            val matchesStatus = selectedStatus == null || seminar.status == selectedStatus

            matchesSearch && matchesStatus
        }
    }
}