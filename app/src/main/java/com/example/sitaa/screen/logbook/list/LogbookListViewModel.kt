package com.example.sitaa.screen.logbook.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.LogbookResponse.Student
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class LogbookListViewModel : ViewModel() {
    var studentList by mutableStateOf<List<Student>>(emptyList())
    var filteredList by mutableStateOf<List<Student>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var searchQuery by mutableStateOf("")
    var currentPage by mutableStateOf(1)
    var hasNextPage by mutableStateOf(false)

    private val apiService = RetrofitClient.createService()

    fun getLogbookStudents(sharedPref: SharedPref, refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (refresh) {
                    currentPage = 1
                    studentList = emptyList()
                }

                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getLogbookStudents(
                    token = "Bearer $token",
                    search = if (searchQuery.isNotBlank()) searchQuery else null,
                    page = currentPage,
                    limit = 20
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    // Transform data with proper photo URLs and remove duplicates
                    val transformedData = response.body()?.data?.distinctBy { it.id }?.map { student ->
                        student.copy(
                            profilePhoto = student.profilePhoto?.let {
                                RetrofitClient.getFullFileUrl(it)
                            }
                        )
                    } ?: emptyList()

                    studentList = if (refresh) {
                        transformedData
                    } else {
                        (studentList + transformedData).distinctBy { it.id }
                    }
                    filterStudentList()

                    // Check if we have more pages
                    hasNextPage = transformedData.isNotEmpty()
                    if (transformedData.isNotEmpty()) {
                        currentPage++
                    }
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
        filterStudentList()

        // If search query changes, we should refresh the list
        if (query.isNotBlank()) {
            currentPage = 1
            studentList = emptyList()
            hasNextPage = false
        }
    }

    fun loadNextPage(sharedPref: SharedPref) {
        if (!isLoading && hasNextPage) {
            getLogbookStudents(sharedPref)
        }
    }

    private fun filterStudentList() {
        filteredList = studentList.filter { student ->
            searchQuery.isBlank() || (
                    student.name.contains(searchQuery, ignoreCase = true) ||
                            student.nim.contains(searchQuery, ignoreCase = true)
                    )
        }.distinctBy { it.id }
    }

    fun refreshData(sharedPref: SharedPref) {
        currentPage = 1
        studentList = emptyList()
        hasNextPage = false
        searchQuery = ""
        getLogbookStudents(sharedPref, true)
    }

    fun clearErrors() {
        errorMessage = null
    }

    fun clearSearchQuery() {
        searchQuery = ""
        filterStudentList()
    }

    // Handle pull to refresh
    fun onRefresh(sharedPref: SharedPref) {
        refreshData(sharedPref)
    }

    // Handle retry on error
    fun onRetry(sharedPref: SharedPref) {
        clearErrors()
        getLogbookStudents(sharedPref, true)
    }

    // Handle state restoration
    fun restoreState(sharedPref: SharedPref) {
        if (studentList.isEmpty() && !isLoading) {
            getLogbookStudents(sharedPref, true)
        }
    }

    // Handle state clearing
    fun clearState() {
        studentList = emptyList()
        filteredList = emptyList()
        currentPage = 1
        hasNextPage = false
        searchQuery = ""
        errorMessage = null
        isLoading = false
    }
}