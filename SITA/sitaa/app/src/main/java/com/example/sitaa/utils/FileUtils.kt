package com.example.sitaa.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object FileUtils {
    private const val DOCUMENTS_DIR = "documents"
    private const val DOWNLOADS_DIR = "downloads"
    private const val TEMP_DIR = "temp"

    fun saveFile(
        context: Context,
        responseBody: ResponseBody,
        fileName: String,
        showFile: Boolean = true,
        mimeType: String = "application/pdf"
    ): Result<File> {
        return try {
            // Create downloads directory if it doesn't exist
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            if (!downloadsDir?.exists()!!) {
                downloadsDir.mkdirs()
            }

            // Create file
            val file = File(downloadsDir, fileName)

            // Write to file
            FileOutputStream(file).use { outputStream ->
                responseBody.byteStream().use { inputStream ->
                    val buffer = ByteArray(4096)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                    outputStream.flush()
                }
            }

            if (showFile) {
                // Create content URI using FileProvider
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                // Create intent to view file
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, mimeType)
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                }

                // Verify there's an app that can handle this intent
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Tidak ada aplikasi yang dapat membuka file ini", Toast.LENGTH_SHORT).show()
                }
            }

            Result.success(file)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    fun openFile(context: Context, file: File, mimeType: String? = null) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType ?: getMimeType(file))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            // Verifikasi aplikasi yang dapat menangani intent
            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Tidak ada aplikasi yang dapat membuka file ini", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal membuka file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun createTempFile(context: Context, fileName: String): File {
        val storageDir = File(context.cacheDir, TEMP_DIR)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File(storageDir, fileName)
    }

    fun getMimeType(file: File): String {
        return when (file.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "doc" -> "application/msword"
            "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            "xls" -> "application/vnd.ms-excel"
            "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "txt" -> "text/plain"
            else -> "*/*"
        }
    }

    fun getExtensionFromMimeType(mimeType: String?): String {
        return when (mimeType) {
            "application/pdf" -> "pdf"
            "application/msword" -> "doc"
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> "docx"
            "application/vnd.ms-excel" -> "xls"
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx"
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "text/plain" -> "txt"
            else -> "tmp"
        }
    }

    fun getFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }

    fun clearTempFiles(context: Context) {
        val tempDir = File(context.cacheDir, TEMP_DIR)
        if (tempDir.exists()) {
            tempDir.listFiles()?.forEach { it.delete() }
        }
    }
}