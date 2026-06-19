package com.example.librarymobile.ui.admin.returns

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.ReturnApiService
import com.example.librarymobile.data.model.request.ReturnRequestDTO
import com.example.librarymobile.data.model.response.ReturnResponseDTO
import com.example.librarymobile.data.model.response.ReturnScanResponseDTO
import kotlinx.coroutines.launch

class ReturnViewModel(private val apiService: ReturnApiService) : ViewModel() {

    var scanResult by mutableStateOf<ReturnScanResponseDTO?>(null)
        private set

    var returnResult by mutableStateOf<ReturnResponseDTO?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    // Hàm gọi API quét thông tin sách
    fun scanBook(copyId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            returnResult = null // Reset biên lai cũ nếu có
            try {
                val response = apiService.scanBookForReturn(copyId)
                if (response.isSuccessful) {
                    scanResult = response.body()
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Không tìm thấy thông tin sách mượn"
                    scanResult = null
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối mạng: ${e.localizedMessage}"
                scanResult = null
            } finally {
                isLoading = false
            }
        }
    }

    // Hàm xác nhận trả sách dứt điểm đơn
    fun confirmReturn(loanId: Long, conditionStatus: String, damageFee: Double, staffNote: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""
            try {
                val request = ReturnRequestDTO(loanId, conditionStatus, damageFee, staffNote)
                val response = apiService.processReturnBook(request)
                if (response.isSuccessful) {
                    returnResult = response.body()
                    scanResult = null // Trả xong xóa kết quả quét tạm thời đi
                } else {
                    errorMessage = response.errorBody()?.string() ?: "Xử lý trả sách thất bại"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi hệ thống: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearMessages() {
        errorMessage = ""
    }
}