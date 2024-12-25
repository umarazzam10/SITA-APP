package com.example.sitaa.screen.defense.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.DefenseResponse.DefenseDetail
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class DefenseListViewModel : ViewModel() {
    var defenseList by mutableStateOf<List<DefenseDetail>>(emptyList())
    var filteredList by mutableStateOf<List<DefenseDetail>>(emptyList())
    var selectedStatus by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")

    private val apiService = RetrofitClient.createService()

    fun getDefenseList(sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getDefenseList(
                    token = "Bearer $token",
                    status = selectedStatus,
                    search = if (searchQuery.isNotBlank()) searchQuery else null
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    // Transform data with proper photo URLs
                    defenseList = response.body()?.data?.map { defense ->
                        defense.copy(
                            student = defense.student.copy(
                                profilePhoto = defense.student.profilePhoto?.let {
                                    RetrofitClient.getFullFileUrl(it)
                                }
                            )
                        )
                    } ?: emptyList()
                    filterDefenseList()
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
        filterDefenseList()
    }

    fun updateSelectedStatus(status: String?, sharedPref: SharedPref) {
        selectedStatus = status
        getDefenseList(sharedPref)
    }

    private fun filterDefenseList() {
        filteredList = defenseList.filter { defense ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                defense.student.name.contains(searchQuery, ignoreCase = true) ||
                        defense.student.nim.contains(searchQuery, ignoreCase = true)
            }

            val matchesStatus = selectedStatus == null || defense.status == selectedStatus

            matchesSearch && matchesStatus
        }
    }
}