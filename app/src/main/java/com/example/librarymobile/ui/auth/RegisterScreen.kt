package com.example.librarymobile.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBackToLogin: () -> Unit,
    authViewModel: AuthViewModel = viewModel() // Khởi tạo ViewModel ở đây
) {
    // Khai báo đầy đủ các State để hứng dữ liệu từ TextField
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Lắng nghe trạng thái đăng ký thành công từ ViewModel
    LaunchedEffect(authViewModel.registerSuccess) {
        if (authViewModel.registerSuccess) {
            // Đăng ký xong tự động quay lại trang đăng nhập
            onBackToLogin()
            // Reset lại trạng thái để lần sau vào không bị dính flag cũ
            authViewModel.registerSuccess = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("HUST LIBRARY", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "TẠO TÀI KHOẢN NHÂN VIÊN",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Vui lòng nhập đầy đủ thông tin nhân sự mới",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Các ô nhập liệu (Giữ nguyên của bro)
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !authViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !authViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Tên đăng nhập") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !authViewModel.isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mật khẩu") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                enabled = !authViewModel.isLoading
            )

            // Hiển thị thông báo lỗi đăng ký nếu Backend trả về lỗi
            authViewModel.loginError?.let { error ->
                Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Nút bấm xử lý API
            Button(
                onClick = {

                    authViewModel.register(username, password, fullName, email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !authViewModel.isLoading
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("XÁC NHẬN ĐĂNG KÝ", fontWeight = FontWeight.Bold)
                }
            }

            TextButton(
                onClick = onBackToLogin,
                modifier = Modifier.fillMaxWidth(),
                enabled = !authViewModel.isLoading
            ) {
                Text("Đã có tài khoản? Đăng nhập ngay")
            }
        }
    }
}