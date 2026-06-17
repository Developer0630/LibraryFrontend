package com.example.librarymobile.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.librarymobile.ui.admin.book.BookManageScreen
import com.example.librarymobile.ui.admin.dashboard.DashboardScreen
import com.example.librarymobile.ui.admin.staff.StaffManageScreen
import com.example.librarymobile.ui.auth.AuthViewModel
import com.example.librarymobile.ui.auth.LoginScreen
import com.example.librarymobile.ui.auth.RegisterScreen
import com.example.librarymobile.ui.student.StudentHomeScreen


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // KHỞI TẠO Ở ĐÂY: Dùng hàm viewModel() để tạo ra thực thể authViewModel
    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Màn hình Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    when (role) {
                        "ADMIN", "STAFF" -> {
                            // Nếu là Admin hoặc Thủ thư -> Vào thẳng Dashboard quản trị
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                        "STUDENT" -> {
                            // Nếu là Sinh viên -> Đá sang màn hình Xem sách & Đặt trước của Sinh viên
                            navController.navigate("student_home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") },
                viewModel = authViewModel
            )
        }

        // Màn hình Register
        composable(route = "register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Màn hình Dashboard
        composable(route = "dashboard") {
            DashboardScreen(navController,onLogout = {
                // Logic đăng xuất: Xóa dữ liệu cũ và quay về màn hình Login
                authViewModel.loginSuccess = false
                authViewModel.currentUserProfile = null
                navController.navigate("login") {
                    popUpTo("dashboard") { inclusive = true }
                }
            })
        }

        composable(route = "student_home") {
            // Lấy ra tên tài khoản sinh viên đã đăng nhập thành công để hiển thị câu chào
            val studentName = authViewModel.currentUserProfile?.username ?: "Sinh viên"

            StudentHomeScreen(
                username = studentName,
                onLogout = {
                    // Logic đăng xuất: Xóa dữ liệu cũ và quay về màn hình Login
                    authViewModel.loginSuccess = false
                    authViewModel.currentUserProfile = null
                    navController.navigate("login") {
                        popUpTo("student_home") { inclusive = true }
                    }
                }
            )
        }

        // Màn hình quản lý nhân viên (Cái mình vừa fix xong data)
        composable("staff_manage") {
            StaffManageScreen(onBack = { navController.popBackStack() })
        }
        composable("book_manage") {
            BookManageScreen(onBack = { navController.popBackStack() })
        }
        // Màn hình quy tắc (Tạm thời để màn hình trống để không bị crash)
//        composable("system_rules") {
//            SystemRulesScreen(onBack = { navController.popBackStack() })
//        }
    }

}