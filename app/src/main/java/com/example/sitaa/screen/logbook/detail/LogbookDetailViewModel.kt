package com.example.sitaa.screen.logbook.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.LogbookResponse
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class LogbookDetailViewModel : ViewModel() {
    var logbookData: LogbookResponse.LogbookData? by mutableStateOf(null)
    var isLoading by mutableStateOf(false)
    var isDownloading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showAddNoteDialog by mutableStateOf(false)
    var selectedEntryId by mutableStateOf<Int?>(null)
    var noteText by mutableStateOf("")
    var currentPage by mutableStateOf(1)
    var hasNextPage by mutableStateOf(false)

    private val apiService = RetrofitClient.createService()

    fun getStudentLogbook(studentId: Int, sharedPref: SharedPref, refresh: Boolean = false) {
        viewModelScope.launch {
            try {
                if (refresh) currentPage = 1
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getStudentLogbook(
                    token = "Bearer $token",
                    studentId = studentId,
                    page = currentPage
                )

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.success == true) {
                        if (refresh) {
                            logbookData = responseData.data
                        } else {
                            val currentEntries = logbookData?.logbook?.entries ?: emptyList()
                            val newEntries = responseData.data.logbook.entries
                            logbookData = responseData.data.copy(
                                logbook = responseData.data.logbook.copy(
                                    entries = currentEntries + newEntries
                                )
                            )
                        }

                        hasNextPage = responseData.data.logbook.entries.isNotEmpty()
                        if (hasNextPage) currentPage++
                    } else {
                        errorMessage = responseData?.message ?: "Gagal memuat data"
                    }
                } else {
                    errorMessage = "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun lockLogbook(studentId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.lockLogbook(
                    token = "Bearer $token",
                    studentId = studentId
                )

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.success == true) {
                        logbookData?.let { currentData ->
                            logbookData = currentData.copy(
                                logbook = currentData.logbook.copy(
                                    isLocked = true
                                )
                            )
                        }
                        onSuccess()
                    } else {
                        errorMessage = responseData?.message ?: "Gagal mengunci logbook"
                    }
                } else {
                    errorMessage = "Gagal mengunci logbook"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun addNote(logbookId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.addLogbookNote(
                    token = "Bearer $token",
                    logbookId = logbookId,
                    note = mapOf("note" to noteText)
                )

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData?.success == true) {
                        logbookData?.let { currentData ->
                            val updatedEntries = currentData.logbook.entries.map { entry ->
                                if (entry.id == logbookId) {
                                    entry.copy(lecturerNotes = noteText)
                                } else {
                                    entry
                                }
                            }
                            logbookData = currentData.copy(
                                logbook = currentData.logbook.copy(
                                    entries = updatedEntries
                                )
                            )
                        }
                        showAddNoteDialog = false
                        noteText = ""
                        selectedEntryId = null
                        onSuccess()
                    } else {
                        errorMessage = responseData?.message ?: "Gagal menambahkan catatan"
                    }
                } else {
                    errorMessage = "Gagal menambahkan catatan"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun downloadLogbook(studentId: Int, sharedPref: SharedPref): ResponseBody? {
        var responseBody: ResponseBody? = null
        viewModelScope.launch {
            try {
                isDownloading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.downloadLogbook(
                    token = "Bearer $token",
                    studentId = studentId
                )

                if (response.isSuccessful) {
                    responseBody = response.body()
                } else {
                    errorMessage = "Gagal mengunduh logbook"
                }
            } catch (e: Exception) {
                errorMessage = e.message
            } finally {
                isDownloading = false
            }
        }
        return responseBody
    }

    fun showAddNoteDialog(entryId: Int) {
        selectedEntryId = entryId
        noteText = logbookData?.logbook?.entries?.find { it.id == entryId }?.lecturerNotes ?: ""
        showAddNoteDialog=true
        }
}