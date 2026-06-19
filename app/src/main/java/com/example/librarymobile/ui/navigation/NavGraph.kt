package com.example.librarymobile.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.librarymobile.data.api.NetworkModule // 1. Import NetworkModule để lấy apiService
import com.example.librarymobile.ui.admin.book.BookManageScreen
import com.example.librarymobile.ui.admin.book.BookViewModel
import com.example.librarymobile.ui.admin.dashboard.DashboardScreen
import com.example.librarymobile.ui.admin.loan.LoanManageScreen // 2. Import đúng màn hình mượn sách
import com.example.librarymobile.ui.admin.loan.LoanViewModel     // 3. Import đúng ViewModel mượn sách
import com.example.librarymobile.ui.admin.staff.StaffManageScreen
import com.example.librarymobile.ui.auth.AuthViewModel
import com.example.librarymobile.ui.auth.LoginScreen
import com.example.librarymobile.ui.auth.RegisterScreen
import com.example.librarymobile.ui.student.StudentHomeScreen
import com.example.librarymobile.ui.student.StudentReservationScreen

import com.example.librarymobile.ui.admin.returns.ReturnScreen
import com.example.librarymobile.ui.admin.returns.ReturnViewModel
import com.example.librarymobile.ui.student.StudentViewModel

// --- BỔ SUNG IMPORT CHO PHẦN THỐNG KÊ BÁO CÁO TẠI ĐÂY ---
import com.example.librarymobile.ui.reports.AdminReportScreen
import com.example.librarymobile.ui.reports.AdminReportViewModel

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    // KHỞI TẠO Ở ĐÂY: Dùng hàm viewModel() để tạo ra thực thể authViewModel
    val authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
    val bookViewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    // 4. Khởi tạo LoanViewModel bằng cách truyền thẳng loanApiService từ Singleton NetworkModule của bro
    val loanViewModel: LoanViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        LoanViewModel(NetworkModule.loanApiService)
    }

    val returnViewModel: ReturnViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        ReturnViewModel(NetworkModule.returnApiService) // Đảm bảo bên NetworkModule của bro đã khai báo returnApiService nhé
    }

    val studentViewModel: StudentViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        StudentViewModel(NetworkModule.studentApiService)
    }

    // --- KHỞI TẠO ADMIN REPORT VIEWMODEL CHUẨN INJECTION ---
    val adminReportViewModel: AdminReportViewModel = androidx.lifecycle.viewmodel.compose.viewModel {
        AdminReportViewModel(NetworkModule.adminApiService)
    }

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
            DashboardScreen(navController, onLogout = {
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
                onNavigateToReservations = {
                    // Điều hướng sang màn hình đặt trước khi sinh viên click nút
                    navController.navigate("student_reservations")
                },
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

        composable("student_reservations") {
            StudentReservationScreen(
                username = authViewModel.currentUserProfile?.username ?: "Sinh viên",
                onBack = {
                    // Bấm nút quay lại trên thanh TopBar thì pop quay về trang chủ
                    navController.popBackStack()
                },
                bookViewModel = bookViewModel
            )
        }

        // Màn hình quản lý nhân viên (Cái mình vừa fix xong data)
        composable("staff_manage") {
            StaffManageScreen(onBack = { navController.popBackStack() })
        }

        composable("book_manage") {
            BookManageScreen(onBack = { navController.popBackStack() })
        }

        // 5. Bổ sung màn hình quản lý mượn sách tại quầy cho Thủ thư
        composable("loan_manage") {
            LoanManageScreen(
                viewModel = loanViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // 6. ĐĂNG KÝ ROUTE CHO MÀN HÌNH TRẢ SÁCH TẠI ĐÂY
        composable("return_manage") {
            ReturnScreen(
                viewModel = returnViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // --- ĐĂNG KÝ ROUTE CHO MÀN HÌNH THỐNG KÊ BÁO CÁO TẠI ĐÂY ---
        composable("admin_report") {
            AdminReportScreen(
                viewModel = adminReportViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Màn hình quy tắc (Tạm thời để màn hình trống để không bị crash)
//        composable("system_rules") {
//            SystemRulesScreen(onBack = { navController.popBackStack() })
//        }
    }
}