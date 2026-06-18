package com.example.librarymobile.ui.admin.book

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.BookRequest
import androidx.compose.runtime.mutableStateListOf
import com.example.librarymobile.data.model.request.ReservationRequestDTO
import com.example.librarymobile.data.model.response.ReservationResponse
import com.example.librarymobile.data.model.response.BookResponse
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    var bookList = mutableStateListOf<BookResponse>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    var reservationMessage by mutableStateOf<String?>(null)
    var reservationList = mutableStateListOf<ReservationResponse>()
    var isReservationLoading by mutableStateOf(false)

    fun fetchBooks(keyword: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = NetworkModule.bookApiService.getBooks(keyword)
                if (response.isSuccessful) {
                    val body = response.body()
                    val books = body?.data // Lấy chuẩn qua tầng .data
                    if (books != null) {
                        bookList.clear()
                        bookList.addAll(books)
                    }
                } else {
                    errorMessage = "Lỗi HTTP: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối sách: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchMyReservations(username: String) {
        viewModelScope.launch {
            isReservationLoading = true
            try {
                val response = NetworkModule.bookApiService.getMyReservations(username)

                if (response.isSuccessful && response.body() != null) {

                    val reservations = response.body()

                    reservationList.clear()
                    if (reservations != null) {
                        reservationList.addAll(reservations)
                    }
                } else {
                    errorMessage = "Không thể lấy danh sách đặt chỗ (Mã: ${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi: ${e.localizedMessage}"
            } finally {
                isReservationLoading = false
            }
        }
    }


    fun createReservation(dto: ReservationRequestDTO, username: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("HUST_DEBUG", "Gửi API đặt sách copyId: ${dto.copy_id} cho user: $username")
                val response = NetworkModule.bookApiService.createReservation(dto, username)

                if (response.isSuccessful && response.body() != null) {
                    val resBody = response.body()

                    // Vì Backend trả về thẳng đối tượng chứa status nên ta kiểm tra status của cuốn sách luôn
                    if (resBody?.status == "Đang giữ") {
                        reservationMessage = "Đặt giữ chỗ thành công! Sách '${resBody.bookTitle}' sẽ được giữ trong 3 ngày."
                        fetchBooks(null) // Reload kho sách
                    } else {
                        errorMessage = "Đặt sách thất bại, trạng thái: ${resBody?.status}"
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Lỗi không xác định từ server"
                    errorMessage = "Đặt sách thất bại (Mã ${response.code()}): $errorBody"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối hoặc Parse dữ liệu: ${e.localizedMessage}"
                android.util.Log.e("HUST_DEBUG", "Crash luồng đặt sách: ", e)
            }
        }
    }

    // SỬA HÀM HỦY ĐẶT TRƯỚC
    fun cancelMyReservation(reservationId: Long, username: String) {
        viewModelScope.launch {
            try {

                val response = NetworkModule.bookApiService.cancelReservation(reservationId, username)

                if (response.isSuccessful) {
                    reservationMessage = "Đã hủy yêu cầu giữ sách thành công!"


                    fetchMyReservations(username)

                    fetchBooks(keyword = null) // Reload kho sách để hồi lại số lượng stock
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Lỗi từ server"
                    errorMessage = "Hủy đặt giữ chỗ thất bại: $errorBody"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi hệ thống khi hủy: ${e.message}"
            }
        }
    }

    fun createBook(request: BookRequest) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.bookApiService.createBook(request)
                if (response.isSuccessful && response.body()?.data != null) {
                    fetchBooks()
                } else {
                    errorMessage = "Không thể thêm sách"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            }
        }
    }

    fun updateBook(id: Long, request: BookRequest) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.bookApiService.updateBook(id, request)
                if (response.isSuccessful && response.body()?.data != null) {
                    fetchBooks()
                } else {
                    errorMessage = "Không thể cập nhật sách"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            }
        }
    }

    fun deleteBook(id: Long) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.bookApiService.deleteBook(id)
                if (response.isSuccessful) {
                    fetchBooks() // Xóa xong reload danh sách
                } else {
                    errorMessage = "Không thể xóa sách"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            }
        }
    }
}