package com.example.librarymobile.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.librarymobile.ui.admin.book.BookManageScreen
import com.example.librarymobile.ui.admin.dashboard.DashboardScreen
import com.example.librarymobile.ui.admin.staff.StaffManageScreen
import com.example.librarymobile.ui.auth.AuthViewModel
import com.example.librarymobile.ui.auth.LoginScreen
import com.example.librarymobile.ui.auth.RegisterScreen


@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // KHỞI TẠO Ở ĐÂY: Dùng hàm viewModel() để tạo ra thực thể authViewModel
    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(navController = navController, startDestination = "login") {
        // Màn hình Login
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("dashboard") },
                onNavigateToRegister = { navController.navigate("register") },
                viewModel = authViewModel // Truyền biến vừa tạo vào đây
            )
        }

        // Màn hình Register
        composable(route = "register") {
            RegisterScreen(
                authViewModel = authViewModel, // Lưu ý: Tên tham số phải khớp với bên RegisterScreen
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Màn hình Dashboard (Tạm thời để trống hoặc gọi DashboardScreen)
        composable(route = "dashboard") {
            DashboardScreen(navController)
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