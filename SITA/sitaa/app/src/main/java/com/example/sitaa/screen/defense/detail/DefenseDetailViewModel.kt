package com.example.sitaa.screen.defense.detail

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sitaa.api.RetrofitClient
import com.example.sitaa.api.response.DefenseResponse
import com.example.sitaa.api.response.DefenseResponse.DefenseDetail
import com.example.sitaa.utils.FileUtils
import com.example.sitaa.utils.SharedPref
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DefenseDetailViewModel : ViewModel() {
    var defense by mutableStateOf<DefenseResponse.DefenseDetail?>(null)
    var isLoading by mutableStateOf(false)
    var isDownloading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Dialog states
    var showRejectDialog by mutableStateOf(false)
    var showApproveDialog by mutableStateOf(false)
    var rejectionReason by mutableStateOf("")
    var suggestedDate by mutableStateOf("")

    private val apiService = RetrofitClient.createService()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getDefenseDetail(defenseId: Int, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getDefenseDetail(
                    token = "Bearer $token",
                    id = defenseId
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    defense = response.body()?.data
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

    fun viewApprovalLetter(defenseId: Int, context: Context, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isDownloading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getDefenseApprovalLetter(
                    token = "Bearer $token",
                    id = defenseId
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        val fileName = "surat_persetujuan_${defense?.student?.nim ?: "temp"}.pdf"
                        val result = FileUtils.saveFile(
                            context = context,
                            responseBody = responseBody,
                            fileName = fileName,
                            showFile = true,
                            mimeType = "application/pdf"
                        )

                        if (result.isFailure) {
                            errorMessage = "Gagal membuka file"
                            Toast.makeText(context, "Gagal membuka file", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    errorMessage = "Gagal mengunduh surat persetujuan"
                    Toast.makeText(context, "Gagal mengunduh file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isDownloading = false
            }
        }
    }

    fun downloadApprovalLetter(defenseId: Int, context: Context, sharedPref: SharedPref) {
        viewModelScope.launch {
            try {
                isDownloading = true
                errorMessage = null

                val token = sharedPref.getAuthToken()
                if (token == null) {
                    errorMessage = "Session telah berakhir"
                    return@launch
                }

                val response = apiService.getDefenseApprovalLetter(
                    token = "Bearer $token",
                    id = defenseId
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        defense?.let { defense ->
                            val result = FileUtils.saveFile(
                                context = context,
                                responseBody = responseBody,
                                fileName = "surat_persetujuan_${defense.student.nim}.pdf",
                                showFile = false,
                                mimeType = "application/pdf"
                            )

                            if (result.isSuccess) {
                                Toast.makeText(context, "File berhasil diunduh", Toast.LENGTH_SHORT).show()
                            } else {
                                errorMessage = "Gagal menyimpan file"
                                Toast.makeText(context, "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    errorMessage = "Gagal mengunduh surat persetujuan"
                    Toast.makeText(context, "Gagal mengunduh file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
                Toast.makeText(context, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isDownloading = false
            }
        }
    }

    fun approveDefense(defenseId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                val token = sharedPref.getAuthToken() ?: return@launch

                if (suggestedDate.isBlank()) {
                    errorMessage = "Tanggal sidang harus diisi"
                    return@launch
                }

                if (!validateDate(suggestedDate)) {
                    errorMessage = "Format tanggal tidak valid (YYYY-MM-DD)"
                    return@launch
                }

                val response = apiService.approveDefense(
                    token = "Bearer $token",
                    id = defenseId,
                    defenseDate = mapOf("defense_date" to suggestedDate)
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    showApproveDialog = false
                    suggestedDate = ""
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menyetujui sidang"
                }
            } catch (e: Exception) {
                errorMessage = e.message
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun rejectDefense(defenseId: Int, sharedPref: SharedPref, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true
                errorMessage = null

                if (rejectionReason.isBlank()) {
                    errorMessage = "Alasan penolakan harus diisi"
                    return@launch
                }

                val token = sharedPref.getAuthToken() ?: return@launch

                // Validate date if provided
                if (suggestedDate.isNotBlank() && !validateDate(suggestedDate)) {
                    errorMessage = "Format tanggal tidak valid (YYYY-MM-DD)"
                    return@launch
                }

                val response = apiService.rejectDefense(
                    token = "Bearer $token",
                    id = defenseId,
                    rejectionRequest = mapOf(
                        "rejection_reason" to rejectionReason,
                        "suggested_date" to suggestedDate
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    resetDialogs()
                    onSuccess()
                } else {
                    errorMessage = response.body()?.message ?: "Gagal menolak sidang"
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
        rejectionReason = ""
        suggestedDate = ""
    }

    fun clearError() {
        errorMessage = null
    }
}