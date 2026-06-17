package com.example.librarymobile.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Lắng nghe trạng thái đăng nhập thành công từ ViewModel
    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            // Lấy thông tin hồ sơ vừa đăng nhập thành công ra
            val profile = viewModel.currentUserProfile
            val roles = profile?.roles ?: emptySet()

            // Phân quyền dựa vào danh sách Roles từ DB trả về
            val userRole = when {
                roles.contains("ADMIN") -> "ADMIN"
                roles.contains("STAFF") -> "STAFF"
                else -> "STUDENT" // Mặc định nếu là sinh viên hoặc rỗng
            }

            // Bắn cái String "ADMIN", "STAFF", hoặc "STUDENT" ra ngoài file điều hướng chính
            onLoginSuccess(userRole)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "HUST LIBRARY", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF005088))

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !viewModel.isLoading
        )

        // Hiển thị lỗi nếu đăng nhập thất bại
        viewModel.loginError?.let { error ->
            Text(text = error, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(username, password)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = !viewModel.isLoading
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("ĐĂNG NHẬP")
            }
        }

        TextButton(onClick = onNavigateToRegister) {
            Text("Chưa có tài khoản? Đăng ký nhân sự")
        }
    }
}