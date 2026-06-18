package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.request.LoanRequestDTO
import com.example.librarymobile.data.model.response.EligibilityResponseDTO
import com.example.librarymobile.data.model.response.LoanResponseDTO
import com.example.librarymobile.data.model.response.StudentSummaryResponseDTO
import retrofit2.Response
import retrofit2.http.*

interface LoanApiService {

    // 1. Quét thẻ sinh viên: Lấy thông tin phiên mượn hiện tại tại quầy
    @GET("api/loans/students/{studentId}/borrow-session")
    suspend fun getStudentBorrowSession(
        @Path("studentId") studentId: Long
    ): Response<StudentSummaryResponseDTO>

    // 2. Kiểm tra điều kiện mượn tự do
    @POST("api/loans/check-eligibility")
    suspend fun checkEligibility(
        @Query("studentId") studentId: Long,
        @Query("copyId") copyId: Long
    ): Response<EligibilityResponseDTO>

    // 3. Tạo mới phiếu mượn (Mượn tự do)
    @POST("api/loans")
    suspend fun createLoan(
        @Body request: LoanRequestDTO
    ): Response<LoanResponseDTO>

    // 4. Xác nhận đã giao sách tận tay sinh viên tại quầy
    @POST("api/loans/{loanId}/confirm-pickup")
    suspend fun confirmLoanPickup(
        @Path("loanId") loanId: Long
    ): Response<LoanResponseDTO>
}