package com.example.librarymobile.ui.admin.loan

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.LoanApiService
import com.example.librarymobile.data.model.request.LoanRequestDTO
import com.example.librarymobile.data.model.response.StudentSummaryResponseDTO
import kotlinx.coroutines.launch
import android.util.Log

class LoanViewModel(private val apiService: LoanApiService) : ViewModel() {

    var studentSession by mutableStateOf<StudentSummaryResponseDTO?>(null)
        private set

    var statusMessage by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Biến lưu trữ ID phiếu mượn động vừa tạo tự động từ DB
    var currentCreatedLoanId by mutableStateOf<Long?>(null)
        private set

    // Quét thẻ sinh viên: GET /api/loans/students/{studentId}/borrow-session
    fun scanStudentCard(studentId: Long) {
        viewModelScope.launch {
            isLoading = true
            statusMessage = ""
            try {
                val response = apiService.getStudentBorrowSession(studentId)
                if (response.isSuccessful) {
                    studentSession = response.body()
                    if (studentSession == null) {
                        statusMessage = "Không tìm thấy thông tin sinh viên."
                    }
                } else {
                    statusMessage = "Lỗi hệ thống: Không thể đọc thông tin thẻ."
                }
            } catch (e: Exception) {
                statusMessage = "Mất kết nối server: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Kiểm tra điều kiện mượn tự do & Tạo phiếu mượn: POST /api/loans
    fun processCheckout(studentId: Long, copyId: Long, note: String?) {
        viewModelScope.launch {
            isLoading = true
            statusMessage = ""
            currentCreatedLoanId = null // Reset lại ID cũ trước khi tạo đơn mới
            Log.d("LIBRARIAN_LOG", "=== BẮT ĐẦU LUỒNG CHO MƯỢN ===")
            Log.d("LIBRARIAN_LOG", "Tham số gửi đi -> studentId: $studentId, copyId: $copyId")

            try {
                Log.d("LIBRARIAN_LOG", "1. Đang gọi API checkEligibility...")
                val eligibilityCheck = apiService.checkEligibility(studentId, copyId)

                Log.d("LIBRARIAN_LOG", "Kết quả API Check -> Code: ${eligibilityCheck.code()}, Body: ${eligibilityCheck.body()}")

                if (eligibilityCheck.isSuccessful) {
                    val eligibility = eligibilityCheck.body()

                    if (eligibility != null && eligibility.pass) {
                        Log.d("LIBRARIAN_LOG", "2. Đủ điều kiện! Đang tiến hành tạo phiếu mượn (createLoan)...")

                        val request = LoanRequestDTO(studentId = studentId, copyId = copyId, note = note)
                        val loanResponse = apiService.createLoan(request)

                        Log.d("LIBRARIAN_LOG", "Kết quả API Create -> Code: ${loanResponse.code()}, Body: ${loanResponse.body()}")

                        if (loanResponse.isSuccessful) {
                            // Hứng lấy ID phiếu mượn thực tế từ Server trả về
                            val createdLoan = loanResponse.body()
                            currentCreatedLoanId = createdLoan?.loanId

                            statusMessage = "Tạo phiếu mượn thành công! Đơn số: #$currentCreatedLoanId"
                            scanStudentCard(studentId)
                        } else {
                            val errorBody = loanResponse.errorBody()?.string()
                            Log.e("LIBRARIAN_LOG", "Server trả về lỗi cụ thể: $errorBody")
                            statusMessage = "Tạo phiếu mượn thất bại: $errorBody"
                        }
                    } else {
                        statusMessage = "Từ chối mượn: ${eligibility?.reason}"
                    }
                } else {
                    statusMessage = "Lỗi phản hồi check điều kiện: HTTP ${eligibilityCheck.code()}"
                }
            } catch (e: Exception) {
                Log.e("LIBRARIAN_LOG", "!!! CRASH HOẶC LỖI LUỒNG GIAO DỊCH !!!", e)
                statusMessage = "Có lỗi xảy ra: ${e.localizedMessage}"
            } finally {
                isLoading = false
                Log.d("LIBRARIAN_LOG", "=== KẾT THÚC LUỒNG CHO MƯỢN ===")
            }
        }
    }

    fun confirmPickup(loanId: Long, studentId: Long) {
        viewModelScope.launch {
            isLoading = true
            statusMessage = ""
            try {
                val response = apiService.confirmLoanPickup(loanId)
                if (response.isSuccessful) {
                    statusMessage = "Đã xác nhận giao sách thành công! Đơn số #$loanId chuyển sang Active."
                    currentCreatedLoanId = null // Xóa ID sau khi đã kích hoạt hoàn tất luồng thành công
                    scanStudentCard(studentId) // Reload lại thông tin session sinh viên để cập nhật UI
                } else {
                    statusMessage = "Không thể xác nhận giao sách (Mã lỗi server: ${response.code()})."
                }
            } catch (e: Exception) {
                statusMessage = "Lỗi kết nối: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}