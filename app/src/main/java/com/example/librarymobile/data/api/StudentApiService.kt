package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.BorrowHistoryDTO
import com.example.librarymobile.data.model.BorrowedItemDTO
import com.example.librarymobile.data.model.FinePaymentDTO
import com.example.librarymobile.data.model.FineSummaryDTO
import com.example.librarymobile.data.model.NotificationDTO
import com.example.librarymobile.data.model.ReservationDTO
import com.example.librarymobile.data.model.ViolationDTO
import com.example.librarymobile.data.model.response.StudentProfileResponseDTO
import retrofit2.Response
import retrofit2.http.GET

interface StudentApiService {

    // 1. Xem thông tin cá nhân (Profile)
    @GET("api/me/profile")
    suspend fun getProfile(): Response<StudentProfileResponseDTO>

    // 2. Xem sách đang mượn
    @GET("api/me/borrowed-items")
    suspend fun getBorrowedItems(): Response<List<BorrowedItemDTO>>

    // 3. Xem lịch sử mượn trả
    @GET("api/me/borrow-history")
    suspend fun getBorrowHistory(): Response<List<BorrowHistoryDTO>>

    // 4. Xem đặt trước hiện có
    @GET("api/me/reservations")
    suspend fun getReservations(): Response<List<ReservationDTO>>

    // 5. Xem vi phạm & công nợ
    @GET("api/me/violations")
    suspend fun getViolations(): Response<List<ViolationDTO>>

    // 6. Xem tổng tiền phạt cần đóng
    @GET("api/me/fines")
    suspend fun getFines(): Response<FineSummaryDTO>

    // 7. Xem lịch sử thanh toán
    @GET("api/me/fine-payments")
    suspend fun getFinePayments(): Response<List<FinePaymentDTO>>

    // 8. Nhận thông báo
    @GET("api/me/notifications")
    suspend fun getNotifications(): Response<List<NotificationDTO>>
}