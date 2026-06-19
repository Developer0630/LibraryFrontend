package com.example.librarymobile.ui.admin.returns

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
fun ReturnScreen(
    viewModel: ReturnViewModel,
    onBack: () -> Unit
) {
    var copyIdInput by remember { mutableStateOf("") }
    var noteInput by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf("NORMAL") }
    var damageFeeInput by remember { mutableStateOf("0") }

    val scanResult = viewModel.scanResult
    val returnResult = viewModel.returnResult
    val errorMessage = viewModel.errorMessage
    val isLoading = viewModel.isLoading

    val conditions = listOf(
        "NORMAL" to "Bình thường (Sách nguyên vẹn)",
        "DAMAGED_LIGHT" to "Hư hỏng nhẹ (Rách trang, vẽ bậy)",
        "DAMAGED_MEDIUM" to "Hư hỏng trung bình",
        "DAMAGED_HEAVY" to "Hư hỏng nặng (Mất bìa, nát sách)",
        "LOST" to "Sinh viên báo mất sách"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quầy Nhận Sách Trả", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
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
                .verticalScroll(rememberScrollState())
        ) {
            // ================= BƯỚC 1: QUÉT MÃ SÁCH =================
            Text("Bước 1: Quét hoặc Nhập mã bản sao sách", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = copyIdInput,
                    onValueChange = { copyIdInput = it },
                    label = { Text("Copy ID (Mã sách)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { if (copyIdInput.isNotEmpty()) viewModel.scanBook(copyIdInput.toLong()) },
                    enabled = !isLoading
                ) {
                    Text("Kiểm tra")
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // hiển thị thông báo lỗi nếu có
            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            // ================= BƯỚC 2: GIÁM ĐỊNH & XÁC NHẬN TRẢ =================
            scanResult?.let { info ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Thông Tin Đơn Mượn Tìm Thấy", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Text("Độc giả: ${info.studentName} (ID: ${info.studentId})")
                        Text("Tên Sách: ${info.bookTitle}")
                        Text("Mã vạch: ${info.barcode ?: "N/A"}")
                        Text("Hạn trả: ${info.dueDate.take(10)}")

                        if (info.overdueDays > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Trễ hạn: ${info.overdueDays} ngày", color = Color.Red, fontWeight = FontWeight.Bold)
                            Text("Phí phạt trễ hạn dự kiến: ${info.overdueFine} VND", color = Color.Red, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Tình trạng hạn: Đúng hạn", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text("Bước 2: Chọn tình trạng thực tế khi nhận sách", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                // Render danh sách tình trạng vật lý (RadioButtons)
                conditions.forEach { (code, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selectedCondition == code),
                            onClick = { selectedCondition = code }
                        )
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = damageFeeInput,
                    onValueChange = { damageFeeInput = it },
                    label = { Text("Phí hư hại tùy biến bổ sung (VND)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Ghi chú của thủ thư") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        viewModel.confirmReturn(
                            loanId = info.loanId,
                            conditionStatus = selectedCondition,
                            damageFee = damageFeeInput.toDoubleOrNull() ?: 0.0,
                            staffNote = noteInput
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Xác Nhận Trả Sách & Tính Phạt")
                }
            }

            // ================= BƯỚC 3: IN BIÊN LAI THÀNH CÔNG =================
            returnResult?.let { receipt ->
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("🧾 BIÊN LAI HOÀN TÁC TRẢ SÁCH", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Giao dịch trả: #${receipt.returnId}")
                        Text("Sinh viên: ${receipt.studentName}")
                        Text("Sách: ${receipt.bookTitle}")
                        Text("Tình trạng ghi nhận: ${receipt.actualCondition}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFA5D6A7))
                        Text("Tiền phạt trễ hạn: ${receipt.lateFee} VND")
                        Text("Tiền phạt hư hại: ${receipt.damageFee} VND")
                        Text("Tổng nợ phát sinh cộng dồn: ${receipt.totalFine} VND", fontWeight = FontWeight.Bold, color = Color.Red, style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }
    }
}