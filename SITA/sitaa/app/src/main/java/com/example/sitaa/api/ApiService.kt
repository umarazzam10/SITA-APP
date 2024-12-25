package com.example.sitaa.api

import com.example.sitaa.api.response.AuthResponse
import com.example.sitaa.api.response.BaseResponse
import com.example.sitaa.api.response.DefenseResponse
import com.example.sitaa.api.response.LogbookResponse
import com.example.sitaa.api.response.NotificationResponse
import com.example.sitaa.api.response.SeminarResponse
import com.example.sitaa.api.response.ThesisResponse
import com.example.sitaa.model.User
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import okhttp3.ResponseBody

interface ApiService {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: Map<String, String>
    ): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): Response<BaseResponse<User>>

    // Thesis Endpoints (Existing)
    @GET("thesis")
    suspend fun getThesisList(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): Response<ThesisResponse>

    @GET("thesis/{id}")
    suspend fun getThesisDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<ThesisResponse.ThesisDetail>>

    @POST("thesis/approve/{id}")
    suspend fun approveThesis(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<ThesisResponse.ThesisDetail>>

    @POST("thesis/reject/{id}")
    suspend fun rejectThesis(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body rejectionRequest: Map<String, String>
    ): Response<BaseResponse<ThesisResponse.ThesisDetail>>

    // Seminar Endpoints
    @GET("seminars")
    suspend fun getSeminarList(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): Response<SeminarResponse>

    @GET("seminars/{id}")
    suspend fun getSeminarDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<SeminarResponse.SeminarDetail>>

    @GET("seminars/{id}/guidance")
    suspend fun getSeminarGuidance(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<LogbookResponse.LogbookData>>

    @GET("seminars/{id}/review")
    suspend fun getSeminarReview(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @POST("seminars/approve/{id}")
    suspend fun approveSeminar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body seminarDate: Map<String, String>
    ): Response<BaseResponse<SeminarResponse.SeminarDetail>>

    @POST("seminars/reject/{id}")
    suspend fun rejectSeminar(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body rejectionRequest: Map<String, String>
    ): Response<BaseResponse<SeminarResponse.SeminarDetail>>

    // Defense Endpoints
    @GET("defense")
    suspend fun getDefenseList(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("search") search: String? = null
    ): Response<DefenseResponse>

    @GET("defense/{id}")
    suspend fun getDefenseDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse<DefenseResponse.DefenseDetail>>

    @GET("defense/approval-letter/{id}")
    suspend fun getDefenseApprovalLetter(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<ResponseBody>

    @POST("defense/approve/{id}")
    suspend fun approveDefense(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body defenseDate: Map<String, String>
    ): Response<BaseResponse<DefenseResponse.DefenseDetail>>

    @POST("defense/reject/{id}")
    suspend fun rejectDefense(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body rejectionRequest: Map<String, String>
    ): Response<BaseResponse<DefenseResponse.DefenseDetail>>

    // Logbook Endpoints
    @GET("logbooks/students")
    suspend fun getLogbookStudents(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<BaseResponse<List<LogbookResponse.Student>>>

    @GET("logbooks/student/{studentId}")
    suspend fun getStudentLogbook(
        @Header("Authorization") token: String,
        @Path("studentId") studentId: Int,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<LogbookResponse>

    @POST("logbooks/lock/{studentId}")
    suspend fun lockLogbook(
        @Header("Authorization") token: String,
        @Path("studentId") studentId: Int,
        @Body lockReason: Map<String, String>? = null
    ): Response<BaseResponse<Nothing>>

    @POST("logbooks/note/{id}")
    suspend fun addLogbookNote(
        @Header("Authorization") token: String,
        @Path("id") logbookId: Int,
        @Body note: Map<String, String>
    ): Response<BaseResponse<LogbookResponse.Entry>>

    @GET("logbooks/download/{studentId}")
    suspend fun downloadLogbook(
        @Header("Authorization") token: String,
        @Path("studentId") studentId: Int,
        @Query("format") format: String? = null
    ): Response<ResponseBody>

    // Notification Endpoints
    @GET("notifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<NotificationResponse>

    @PUT("notifications/{id}/read")
    suspend fun markNotificationAsRead(
        @Header("Authorization") token: String,
        @Path("id") notificationId: Int
    ): Response<BaseResponse<Nothing>>

    @PUT("notifications/mark-all-read")
    suspend fun markAllNotificationsAsRead(
        @Header("Authorization") token: String
    ): Response<BaseResponse<Nothing>>

    @GET("notifications/unread-count")
    suspend fun getUnreadNotificationsCount(
        @Header("Authorization") token: String
    ): Response<BaseResponse<Map<String, Int>>>

    // Profile Endpoints
    @Multipart
    @PUT("users/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("username") username: String? = null,
        @Part("password") password: String? = null,
        @Part profilePhoto: MultipartBody.Part? = null
    ): Response<BaseResponse<User>>

    // File Download Endpoints
    @GET("thesis/download/{filename}")
    suspend fun downloadThesisFile(
        @Header("Authorization") token: String,
        @Path("filename") filename: String
    ): Response<ResponseBody>
}