package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.response.LoanSummaryResponseDTO
import com.example.librarymobile.data.model.response.StudentActivityResponseDTO
import com.example.librarymobile.data.model.response.TopBookResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AdminApiService {

    // 1. Cập nhật nhận tham số start, end và kiểu trả về mới
    @GET("api/reports/loan-summary")
    suspend fun getLoanSummary(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<LoanSummaryResponseDTO>

    // 2. Cập nhật nhận tham số start, end và kiểu trả về mới
    @GET("api/reports/top-books")
    suspend fun getTopBooks(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<TopBookResponseDTO>>

    // 3. Cập nhật nhận tham số start, end và kiểu trả về mới
    @GET("api/reports/student-activity")
    suspend fun getStudentActivity(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<List<StudentActivityResponseDTO>>

    // Giữ nguyên hàm xuất báo cáo nếu có dùng
    @POST("api/reports/export")
    suspend fun exportReport(
        @Query("format") format: String
    ): Response<Map<String, String>>
}