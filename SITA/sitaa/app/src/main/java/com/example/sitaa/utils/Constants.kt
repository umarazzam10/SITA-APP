package com.example.sitaa.utils

object Constants {
    // SharedPreferences Keys
    const val PREF_NAME = "SITAA_PREF"
    const val KEY_TOKEN = "user_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_ROLE = "user_role"

    // API Related
    const val AUTHORIZATION = "Authorization"
    const val BEARER = "Bearer "

    // Status Constants
    const val STATUS_PENDING = "pending"
    const val STATUS_APPROVED = "approved"
    const val STATUS_REJECTED = "rejected"

    // File Types
    const val FILE_TYPE_PDF = "application/pdf"
    const val FILE_TYPE_DOC = "application/msword"
    const val FILE_TYPE_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    // Navigation Routes
    object Routes {
        const val SPLASH = "splash"
        const val LOGIN = "login"
        const val HOME = "home"
        const val THESIS_LIST = "thesis_list"
        const val THESIS_DETAIL = "thesis_detail/{thesisId}"
        const val SEMINAR_LIST = "seminar_list"
        const val SEMINAR_DETAIL = "seminar_detail/{seminarId}"
        const val DEFENSE_LIST = "defense_list"
        const val DEFENSE_DETAIL = "defense_detail/{defenseId}"
        const val LOGBOOK_LIST = "logbook_list"
        const val LOGBOOK_DETAIL = "logbook_detail/{studentId}"
        const val PROFILE = "profile"
        const val NOTIFICATION = "notification"
    }

    // Bottom Navigation
    object BottomNav {
        const val HOME = "Home"
        const val NOTIFICATION = "Notifikasi"
        const val PROFILE = "Profile"
    }

    // Error Messages
    const val ERROR_GENERIC = "Terjadi kesalahan. Silakan coba lagi."
    const val ERROR_NETWORK = "Koneksi gagal. Periksa koneksi internet Anda."
    const val ERROR_LOGIN = "Username atau password salah."
    const val ERROR_UNAUTHORIZED = "Sesi Anda telah berakhir. Silakan login kembali."
    const val ERROR_FILE_DOWNLOAD = "Gagal mengunduh file."
    const val ERROR_FILE_UPLOAD = "Gagal mengunggah file."
    const val ERROR_FILE_NOT_FOUND = "File tidak ditemukan."

    // Success Messages
    const val SUCCESS_PROFILE_UPDATE = "Profil berhasil diperbarui."
    const val SUCCESS_PASSWORD_UPDATE = "Password berhasil diperbarui."
    const val SUCCESS_FILE_DOWNLOAD = "File berhasil diunduh."
    const val SUCCESS_LOGBOOK_LOCK = "Logbook berhasil dikunci."
    const val SUCCESS_LOGBOOK_NOTE = "Catatan berhasil ditambahkan."
}