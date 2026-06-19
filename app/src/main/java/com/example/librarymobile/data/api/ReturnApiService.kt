package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.request.ReturnRequestDTO
import com.example.librarymobile.data.model.response.ReturnResponseDTO
import com.example.librarymobile.data.model.response.ReturnScanResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ReturnApiService {

    // Bước 1: Quét mã sách vật lý để tìm phiếu mượn hoạt động
    @POST("api/returns/scan")
    suspend fun scanBookForReturn(
        @Query("copyId") copyId: Long
    ): Response<ReturnScanResponseDTO>

    // Bước 2 + 3: Xác nhận trả sách, áp phí phạt và đóng đơn
    @POST("api/returns")
    suspend fun processReturnBook(
        @Body request: ReturnRequestDTO
    ): Response<ReturnResponseDTO>
}