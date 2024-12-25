package com.example.sitaa.screen.thesis.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.ThesisResponse.ThesisDetail
import com.example.sitaa.utils.FileUtils
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch

class ThesisDetailViewModel : ViewModel() {
    var thesis by mutableStateOf<ThesisDetail?>(null)
    var isLoading by mutableStateOf(false)
    var isDownloading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var showRejectDialog by mutableStateOf(false)
    var rejectionReason by mutableStateOf("")

    private val apiService = RetrofitClient.createService()

    fun getThesisDetail(thesisId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getThesisDetail(
                    token = "Bearer $token",
                    id = thesisId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    // Transform data with proper photo URL
                    response.body()?.data?.let { detail ->
                        thesis = detail.copy(
                            student = detail.student.copy(
                                profilePhoto = detail.student.profilePhoto?.let {
                                    RetrofitClient.getFullFileUrl(it)
                                } ?: ""  // Memberikan default empty string jika null
                            ),
                            // Memastikan semua required fields memiliki nilai default
                            attachmentFile = detail.attachmentFile ?: "",
                            createdAt = detail.createdAt ?: "",  // Memberikan default empty string
                            updatedAt = detail.updatedAt ?: "",  // Memberikan default empty string
                            rejectionReason = detail.rejectionReason ?: "" // Memberikan default empty string
                        )
                    }
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat data"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun approveThesis(thesisId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken() ?: return@launch

                val response = apiService.approveThesis(
                    token = "Bearer $token",
                    id = thesisId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menyetujui pengajuan"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun rejectThesis(thesisId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                if (rejectionReason.isBlank()) {
                    errorMessage = "Alasan penolakan harus diisi"
                    return@launch
                }

                val token = sharedPref.getAuthToken() ?: return@launch

                val response = apiService.rejectThesis(
                    token = "Bearer $token",
                    id = thesisId,
                    rejectionRequest = mapOf("rejection_reason" to rejectionReason)
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    showRejectDialog = false
                    rejectionReason = ""
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menolak pengajuan"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun downloadThesis(fileName: String, context: Context, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isDownloading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.downloadThesisFile(
                    token = "Bearer $token",
                    filename = fileName
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        thesis?.let { thesis ->
                            val result = FileUtils.saveFile(
                                context = context,
                                responseBody = responseBody,
                                fileName = "thesis_${thesis.student.nim}.pdf",
                                showFile = true,
                                mimeType = "application/pdf"
                            )

                            if (result.isSuccess) {
                                Toast.makeText(context, "File berhasil diunduh", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    errorMessage = "Gagal mengunduh file"
                    Toast.makeText(context, "Gagal mengunduh file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                isDownloading = false
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }

    fun resetDialogs() {
        showRejectDialog = false
        rejectionReason = ""
    }
}