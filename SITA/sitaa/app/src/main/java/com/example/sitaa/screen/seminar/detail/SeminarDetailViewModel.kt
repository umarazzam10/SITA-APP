package com.example.sitaa.screen.seminar.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.SeminarResponse.SeminarDetail
import com.example.sitaa.api.response.LogbookResponse.LogbookData
import com.example.sitaa.utils.FileUtils
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.text.SimpleDateFormat
import java.util.Locale

class SeminarDetailViewModel : ViewModel() {
    var seminar by mutableStateOf<SeminarDetail?>(null)
    var guidanceHistory by mutableStateOf<LogbookData?>(null)
    var isLoading by mutableStateOf(false)
    var isDownloading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Dialog states
    var showGuidanceDialog by mutableStateOf(false)
    var showRejectDialog by mutableStateOf(false)
    var showApproveDialog by mutableStateOf(false)
    var rejectionReason by mutableStateOf("")
    var suggestedDate by mutableStateOf("")

    private val apiService = RetrofitClient.createService()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getSeminarDetail(seminarId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getSeminarDetail(
                    token = "Bearer $token",
                    id = seminarId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.data?.let { detail ->
                        seminar = detail.copy(
                            student = detail.student.copy(
                                profilePhoto = detail.student.profilePhoto?.let {
                                    RetrofitClient.getFullFileUrl(it)
                                }
                            )
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

    fun getGuidanceHistory(seminarId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getSeminarGuidance(
                    token = "Bearer $token",
                    id = seminarId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    guidanceHistory = response.body()?.data
                    showGuidanceDialog = true
                } else {
                    errorMessage = response.body()?.message ?: "Gagal memuat riwayat bimbingan"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun downloadThesisReview(context: Context, seminarId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isDownloading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getSeminarReview(
                    token = "Bearer $token",
                    id = seminarId
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val result = FileUtils.saveFile(
                            context = context,
                            responseBody = responseBody,
                            fileName = "review_ta_${seminar?.student?.nim}.pdf",
                            showFile = true,
                            mimeType = "application/pdf"
                        )

                        if (result.isSuccess) {
                            Toast.makeText(context, "File berhasil diunduh", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Gagal mengunduh file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Gagal mengunduh file: ${e.message}"
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            } finally {
                isDownloading = false
            }
        }
    }

    fun approveSeminar(seminarId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken() ?: return@launch

                if (suggestedDate.isBlank()) {
                    errorMessage = "Tanggal seminar harus diisi"
                    return@launch
                }

                // Validate date format
                if (!validateDate(suggestedDate)) {
                    errorMessage = "Format tanggal tidak valid (YYYY-MM-DD)"
                    return@launch
                }

                val response = apiService.approveSeminar(
                    token = "Bearer $token",
                    id = seminarId,
                    seminarDate = mapOf("seminar_date" to suggestedDate)
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    showApproveDialog = false
                    suggestedDate = ""
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menyetujui seminar"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun rejectSeminar(seminarId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken() ?: return@launch

                if (rejectionReason.isBlank()) {
                    errorMessage = "Alasan penolakan harus diisi"
                    return@launch
                }

                // Validate date if provided
                if (suggestedDate.isNotBlank() && !validateDate(suggestedDate)) {
                    errorMessage = "Format tanggal tidak valid (YYYY-MM-DD)"
                    return@launch
                }

                val response = apiService.rejectSeminar(
                    token = "Bearer $token",
                    id = seminarId,
                    rejectionRequest = mapOf(
                        "rejection_reason" to rejectionReason,
                        "suggested_date" to suggestedDate
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    resetDialogs()
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menolak seminar"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun validateDate(date: String): Boolean {
        return try {
            dateFormat.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun resetDialogs() {
        showRejectDialog = false
        showApproveDialog = false
        showGuidanceDialog = false
        rejectionReason = ""
        suggestedDate = ""
    }

    fun clearError() {
        errorMessage = null
    }
}