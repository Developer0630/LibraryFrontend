package com.example.librarymobile.ui.student

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.StudentApiService
import com.example.librarymobile.data.model.* // Import các DTO đã tạo ở bước trước
import com.example.librarymobile.data.model.response.StudentProfileResponseDTO
import kotlinx.coroutines.launch

class StudentViewModel(private val apiService: StudentApiService) : ViewModel() {

    var profileState by mutableStateOf<StudentProfileResponseDTO?>(null)
    var borrowedItemsState by mutableStateOf<List<BorrowedItemDTO>>(emptyList())
    var borrowHistoryState by mutableStateOf<List<BorrowHistoryDTO>>(emptyList())
    var violationsState by mutableStateOf<List<ViolationDTO>>(emptyList())
    var isLoading by mutableStateOf(false)

    var reservationsState by mutableStateOf<List<ReservationDTO>>(emptyList())
    var fineSummaryState by mutableStateOf<FineSummaryDTO?>(null)
    var finePaymentsState by mutableStateOf<List<FinePaymentDTO>>(emptyList())
    var notificationsState by mutableStateOf<List<NotificationDTO>>(emptyList())

    // Hàm load toàn bộ dữ liệu khi sinh viên vào dashboard tra cứu
    fun loadStudentDashboardData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val profileRes = apiService.getProfile()
                if (profileRes.isSuccessful) profileState = profileRes.body()

                val borrowedRes = apiService.getBorrowedItems()
                if (borrowedRes.isSuccessful) borrowedItemsState = borrowedRes.body() ?: emptyList()

                val historyRes = apiService.getBorrowHistory()
                if (historyRes.isSuccessful) borrowHistoryState = historyRes.body() ?: emptyList()

                val violationRes = apiService.getViolations()
                if (violationRes.isSuccessful) violationsState = violationRes.body() ?: emptyList()

                val resRes = apiService.getReservations()
                if (resRes.isSuccessful) reservationsState = resRes.body() ?: emptyList()

                val finesRes = apiService.getFines()
                if (finesRes.isSuccessful) fineSummaryState = finesRes.body()

                val paymentsRes = apiService.getFinePayments()
                if (paymentsRes.isSuccessful) finePaymentsState = paymentsRes.body() ?: emptyList()

                val notiRes = apiService.getNotifications()
                if (notiRes.isSuccessful) notificationsState = notiRes.body() ?: emptyList()
            } catch (e: Exception) {
                // Xử lý lỗi kết nối tại đây
            } finally {
                isLoading = false
            }
        }
    }
}