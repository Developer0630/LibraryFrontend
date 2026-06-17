package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.request.*
import com.example.librarymobile.data.model.response.*
import retrofit2.Response
import retrofit2.http.*

interface BookApiService {
    @GET("api/books")
    suspend fun getBooks(
        @Query("q") keyword: String? = null
    ): Response<BaseResponse<List<BookResponse>>>

    @POST("api/books")
    suspend fun createBook(
        @Body request: BookRequest
    ): Response<BaseResponse<BookResponse>>

    @PUT("api/books/{id}")
    suspend fun updateBook(
        @Path("id") id: Long,
        @Body request: BookRequest
    ): Response<BaseResponse<BookResponse>>

    @DELETE("api/books/{id}")
    suspend fun deleteBook(
        @Path("id") id: Long
    ): Response<BaseResponse<Unit>>

    // Quản lý bản in (Book Copies)
    @GET("api/book-copies")
    suspend fun getBookCopies(@Query("bookId") bookId: Long): Response<List<BookCopyResponse>>

    @PATCH("api/book-copies/{copyId}/circulation-status")
    suspend fun updateCopyStatus(
        @Path("copyId") copyId: Long,
        @Query("status") status: String
    ): Response<BookCopyResponse>

    // Lấy danh sách đặt trước của sinh viên hiện tại
    @GET("api/reservations")
    suspend fun getMyReservations(): Response<List<ReservationResponse>>

    // Hủy đặt trước sách dựa vào ID phiếu đặt
    @DELETE("api/reservations/{reservationId}")
    suspend fun cancelReservation(
        @Path("reservationId") reservationId: Long
    ): Response<String>
}