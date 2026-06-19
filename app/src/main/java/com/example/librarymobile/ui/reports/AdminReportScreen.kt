package com.example.librarymobile.ui.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarymobile.data.api.NetworkModule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportScreen(
    viewModel: AdminReportViewModel = viewModel { AdminReportViewModel(NetworkModule.adminApiService) },
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) { viewModel.fetchAllReports() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("THỐNG KÊ HỆ THỐNG", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Nút Xuất Báo Cáo
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.exportFile("PDF") }, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB71C1C))) {
                            Icon(Icons.Default.PictureAsPdf, null)
                            Text(" Xuất PDF")
                        }
                        Button(onClick = { viewModel.exportFile("EXCEL") }, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))) {
                            Icon(Icons.Default.Description, null)
                            Text(" Xuất Excel")
                        }
                    }
                }

                // 2. Thẻ Hiệu Suất Mượn Trả (Đã map chuẩn biến Backend)
                item {
                    ReportCard(title = "Hiệu suất mượn trả") {
                        val s = viewModel.loanSummary
                        StatRow("Tổng số lượt mượn", "${s?.totalLoans ?: 0}")
                        StatRow("Tổng số lượt trả", "${s?.totalReturns ?: 0}")
                        StatRow("Trả đúng hạn", "${s?.onTimeReturns ?: 0}", color = Color(0xFF2E7D32))
                        StatRow("Trả quá hạn", "${s?.overdueReturns ?: 0}", color = Color.Red)
                        StatRow("Tỷ lệ đúng hạn", "${s?.onTimeRate ?: 0.0}%", color = Color(0xFF1A237E), isBold = true)
                    }
                }

                // --- 3. Khối Hoạt Động Theo Khoa / Lớp ---
                item {
                    Text("Mức độ hoạt động theo Khoa / Lớp", fontWeight = FontWeight.Bold)
                }

// Thay vì bọc if-else ngoài itemsIndexed, ta viết tách biệt hẳn ra trong LazyColumn DSL:
                if (viewModel.studentActivities.isEmpty()) {
                    item {
                        Text("Chưa có dữ liệu phân tích lớp.", color = Color.Gray, fontSize = 13.sp)
                    }
                } else {
                    // Gọi thẳng itemsIndexed mà không bọc trong ngoặc nhọn của else nữa
                    itemsIndexed(viewModel.studentActivities) { index, act ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Lớp: ${act.className}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Ngành: ${act.department} • SV mượn: ${act.activeStudentsCount}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Text("${act.totalBorrows} lượt", fontWeight = FontWeight.Black, color = Color(0xFF1A237E), fontSize = 14.sp)
                            }
                        }
                    }
                }

                // 4. Bảng Xếp Hạng Sách mượn nhiều nhất
                item {
                    Text("Top sách được mượn nhiều nhất", fontWeight = FontWeight.Bold)
                }
                if (viewModel.topBooks.isEmpty()) {
                    item { Text("Chưa có số liệu xếp hạng sách.", color = Color.Gray, fontSize = 13.sp) }
                } else {
                    itemsIndexed(viewModel.topBooks) { index, book ->
                        Card(Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("#${index + 1}", fontWeight = FontWeight.Black, color = Color(0xFF1A237E), modifier = Modifier.width(30.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(book.title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(book.author, fontSize = 12.sp, color = Color.Gray)
                                }
                                Text("${book.borrowCount} lượt", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String, color: Color = Color.Black, isBold: Boolean = false) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.DarkGray)
        Text(value, color = color, fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold)
    }
}

@Composable
fun ReportCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A237E), fontSize = 16.sp)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            content()
        }
    }
}