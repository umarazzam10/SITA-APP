package com.example.sitaa.utils

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SharedPref(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME, Context.MODE_PRIVATE
    )

    suspend fun saveAuthToken(token: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }

    suspend fun getAuthToken(): String? = withContext(Dispatchers.IO) {
        prefs.getString(Constants.KEY_TOKEN, null)
    }

    suspend fun saveUserId(userId: Int) = withContext(Dispatchers.IO) {
        prefs.edit().putInt(Constants.KEY_USER_ID, userId).apply()
    }

    suspend fun getUserId(): Int = withContext(Dispatchers.IO) {
        prefs.getInt(Constants.KEY_USER_ID, -1)
    }

    suspend fun saveUserRole(role: String) = withContext(Dispatchers.IO) {
        prefs.edit().putString(Constants.KEY_USER_ROLE, role).apply()
    }

    suspend fun getUserRole(): String? = withContext(Dispatchers.IO) {
        prefs.getString(Constants.KEY_USER_ROLE, null)
    }

    suspend fun clearSession() = withContext(Dispatchers.IO) {
        prefs.edit().clear().apply()
    }

    suspend fun isLoggedIn(): Boolean = withContext(Dispatchers.IO) {
        getAuthToken() != null
    }

    fun getAuthTokenSync(): String? {
        return prefs.getString(Constants.KEY_TOKEN, null)
    }

    companion object {
        @Volatile
        private var instance: SharedPref? = null

        fun getInstance(context: Context): SharedPref {
            return instance ?: synchronized(this) {
                instance ?: SharedPref(context).also { instance = it }
            }
        }
    }
}