package com.example.librarymobile.ui.admin.book

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.BookRequest

import com.example.librarymobile.data.model.response.BookResponse
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    var bookList = mutableStateListOf<BookResponse>()
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun fetchBooks(keyword: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = NetworkModule.bookApiService.getBooks(keyword)

                if (response.isSuccessful) {
                    // Lớp 1: Lấy Body của Retrofit
                    val body = response.body()

                    // Lớp 2: Lấy field 'data' bên trong BaseResponse của Backend
                    val books = body?.data

                    if (books != null) {
                        bookList.clear()
                        bookList.addAll(books)
                    }
                } else {
                    errorMessage = "Lỗi HTTP: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối: ${e.message}"
            } finally {
                isLoading = false
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