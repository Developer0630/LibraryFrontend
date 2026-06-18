package com.example.librarymobile.ui.admin.loan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanManageScreen(viewModel: LoanViewModel,onBack: () -> Unit) {
    var studentIdInput by remember { mutableStateOf("") }
    var copyIdInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    // 🌟 ĐƯA LÊN TRÊN: Quản lý trạng thái nhập thủ công tập trung ở đầu Composable
    var manualLoanIdInput by remember { mutableStateOf("") }

    val studentSession = viewModel.studentSession
    val statusMessage = viewModel.statusMessage
    val isLoading = viewModel.isLoading
    val currentLoanId = viewModel.currentCreatedLoanId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quầy Xử Lý Mượn Sách") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                // Thêm scroll để màn hình vuốt xuống dưới được, không lo bị che khuất nội dung hay mất Card
                .verticalScroll(rememberScrollState())
        ) {
            // --- Khu vực 1: Quét thẻ sinh viên ---
            Text(text = "Bước 1: Quét / Nhập ID Thẻ Sinh Viên", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = studentIdInput,
                    onValueChange = { studentIdInput = it },
                    label = { Text("ID Sinh viên") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (studentIdInput.isNotEmpty()) {
                            viewModel.scanStudentCard(studentIdInput.toLong())
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Quét Thẻ")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // --- Khu vực 2 & 3: Chỉ hiển thị khi đã tìm thấy sinh viên ---
            studentSession?.let { session ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Thông Tin Phiên Mượn Hiện Tại", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Tên sinh viên: ${session.fullName}")
                        Text("Mã sinh viên: ${session.studentCode}")
                        Text("Trạng thái thẻ: ${session.status}", color = if (session.status == "ACTIVE") Color(0xFF2E7D32) else Color.Red)
                        Text("Số sách đang mượn: ${session.activeLoans} cuốn")
                        Text("Số đơn đang đặt giữ: ${session.pendingReservations} cuốn")
                        Text("Phí phạt chưa thanh toán: ${session.totalDebt} VND")
                        Text("Hạn mức mượn còn lại: ${session.remainingLimit} cuốn", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Khu vực 3: Nhập thông tin tạo yêu cầu mượn (Giai đoạn Pending)
                Text(text = "Bước 2: Tạo Đơn Đăng Ký Mượn Sách", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = copyIdInput,
                    onValueChange = { copyIdInput = it },
                    label = { Text("Mã bản sao (Copy ID)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Ghi chú của thủ thư (Không bắt buộc)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (copyIdInput.isNotEmpty()) {
                            println("DEBUG_BORROW: StudentID = ${session.id}, CopyID = $copyIdInput")
                            viewModel.processCheckout(
                                studentId = session.id,
                                copyId = copyIdInput.toLong(),
                                note = noteInput.ifEmpty { null }
                            )
                            copyIdInput = ""
                            noteInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = session.status == "ACTIVE" && session.remainingLimit > 0 && !isLoading
                ) {
                    Text("Xác Nhận Tạo Đơn Mượn (Pending)")
                }

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // --- Khu vực 4: Xác Nhận Giao Sách Tận Tay (Luôn hiển thị khi đã tìm thấy sinh viên) ---
                Text(text = "Bước 3: Xác Nhận Giao Sách Tận Tay", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE65100))
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF3E0), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = if (currentLoanId != null) "Đơn mượn vừa tạo thành công: #$currentLoanId" else "Nhập Mã Đơn Mượn (Loan ID) để kích hoạt",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = if (currentLoanId != null) currentLoanId.toString() else manualLoanIdInput,
                                onValueChange = { manualLoanIdInput = it },
                                label = { Text("Mã đơn mượn (Loan ID)") },
                                enabled = currentLoanId == null,
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val finalLoanId = currentLoanId ?: manualLoanIdInput.toLongOrNull()
                                    if (finalLoanId != null) {
                                        viewModel.confirmPickup(loanId = finalLoanId, studentId = session.id)
                                        manualLoanIdInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                enabled = !isLoading && (currentLoanId != null || manualLoanIdInput.isNotEmpty())
                            ) {
                                Text("Đã Giao Sách")
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Hãy trao sách vật lý cho sinh viên và nhấn nút để kích hoạt hạn trả 14 ngày.", style = MaterialTheme.typography.bodySmall, color = Color.DarkGray)
                    }
                }
            }

            // --- Khu vực 5: Hiển thị thông báo kết quả/lỗi từ server ---
            if (statusMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = if (statusMessage.contains("thành công")) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusMessage,
                        color = if (statusMessage.contains("thành công")) Color(0xFF2E7D32) else Color.Red,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}