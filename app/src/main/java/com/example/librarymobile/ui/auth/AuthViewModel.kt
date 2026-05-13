package com.example.librarymobile.ui.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.CreateStaffRequest
import com.example.librarymobile.data.model.request.LoginRequest
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Trạng thái UI
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    var registerSuccess by mutableStateOf(false)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val request = LoginRequest(username, password)
                val response = NetworkModule.apiService.login(request)

                if (response.success) {
                    loginSuccess = true
                } else {
                    loginError = response.message
                }
            } catch (e: Exception) {
                loginError = "Lỗi kết nối: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
    fun register(username: String, pass: String, name: String, email: String) {
        viewModelScope.launch {
            isLoading = true
            registerSuccess = false // Reset state trước khi gọi
            try {
                val request = CreateStaffRequest(username, pass, name, email, roleId = 2)
                val response = NetworkModule.apiService.register(request)
                if (response.success) {
                    registerSuccess = true // Đánh dấu thành công
                } else {
                    loginError = response.message
                }
            } catch (e: Exception) {
                loginError = "Lỗi đăng ký: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}