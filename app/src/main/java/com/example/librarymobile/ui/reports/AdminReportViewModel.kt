package com.example.librarymobile.ui.reports

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.AdminApiService
// Xóa các dòng import com.library.library_manager... cũ đi và thay bằng:
import com.example.librarymobile.data.model.response.LoanSummaryResponseDTO
import com.example.librarymobile.data.model.response.StudentActivityResponseDTO
import com.example.librarymobile.data.model.response.TopBookResponseDTO
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdminReportViewModel(private val apiService: AdminApiService) : ViewModel() {
    // Đổi kiểu dữ liệu sang bộ ResponseDTO mới cho khớp Backend
    var loanSummary by mutableStateOf<LoanSummaryResponseDTO?>(null)
    var topBooks by mutableStateOf<List<TopBookResponseDTO>>(emptyList())
    var studentActivities by mutableStateOf<List<StudentActivityResponseDTO>>(emptyList())

    var isLoading by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    fun fetchAllReports() {
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Tạo chuỗi định dạng thời gian ISO_LOCAL_DATE_TIME (Ví dụ: 2026-01-01T00:00:00)
                val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                val startStr = LocalDateTime.of(2026, 1, 1, 0, 0, 0).format(formatter)
                val endStr = LocalDateTime.now().format(formatter)

                // 2. Truyền start và end vào đúng các hàm API theo thiết kế của ReportController
                val loanRes = apiService.getLoanSummary(startStr, endStr)
                if (loanRes.isSuccessful) loanSummary = loanRes.body()

                val topRes = apiService.getTopBooks(startStr, endStr)
                if (topRes.isSuccessful) topBooks = topRes.body() ?: emptyList()

                val actRes = apiService.getStudentActivity(startStr, endStr)
                if (actRes.isSuccessful) studentActivities = actRes.body() ?: emptyList()

            } catch (e: Exception) {
                message = "Lỗi kết nối dữ liệu thống kê: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun exportFile(format: String) {
        viewModelScope.launch {
            try {
                val res = apiService.exportReport(format)
                if (res.isSuccessful) {
                    message = "Đã gửi link tải $format vào email của bạn!"
                }
            } catch (e: Exception) {
                message = "Lỗi xuất file"
            }
        }
    }
}