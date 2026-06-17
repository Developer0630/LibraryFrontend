package com.example.librarymobile.ui.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.LoginRequest
import com.example.librarymobile.data.model.request.RegisterRequest
import com.example.librarymobile.data.model.response.LoginResponse
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    // Trạng thái UI
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    var registerSuccess by mutableStateOf(false)
    var currentUserProfile by mutableStateOf<LoginResponse?>(null)
    fun login(username: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            loginSuccess = false
            try {


                val request = LoginRequest(username, password)
                val response = NetworkModule.apiService.login(request)
                if (response.data != null) {
                    currentUserProfile = response.data
                    loginSuccess = true
                } else {
                    loginError = response.message ?: "Đăng nhập thất bại"
                }


                /*// GIẢ LẬP ĐĂNG NHẬP THÀNH CÔNG ĐỂ VÀO DASHBOARD
                kotlinx.coroutines.delay(1000)
                loginSuccess = true
                */


            } catch (e: Exception) {
                // test giao diện lỗi thì mới để dòng này
                 loginError = "Lỗi kết nối: ${e.localizedMessage}"
                // Ép thành công luôn kể cả khi có lỗi kết nối
//                loginSuccess = true
            } finally {
                isLoading = false
            }
        }
    }
    fun register(username: String, pass: String, name: String, email: String) {
        viewModelScope.launch {
            isLoading = true
            registerSuccess = false
            loginError = null
            try {

                val request = RegisterRequest(
                    username = username,
                    password = pass,
                    fullName = name,
                    email = email,
                    role = "STAFF"
                )

                val response = NetworkModule.apiService.register(request)

                if (response.data != null || response.message == null) {
                    registerSuccess = true // Kích hoạt LaunchedEffect ở giao diện quay về Login
                } else {
                    loginError = response.message ?: "Đăng ký không thành công!"
                }
            } catch (e: Exception) {
                loginError = "Lỗi kết nối: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}