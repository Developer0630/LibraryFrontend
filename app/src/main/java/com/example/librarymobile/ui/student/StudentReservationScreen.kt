package com.example.librarymobile.ui.student

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.librarymobile.ui.admin.book.BookViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentReservationScreen(
    username: String,
    onBack: () -> Unit,
    bookViewModel: BookViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Tự động tải danh sách phiếu đặt khi vào màn hình
    LaunchedEffect(username) {
        bookViewModel.fetchMyReservations(username)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sách Đã Đặt Trước", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (bookViewModel.isReservationLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (bookViewModel.reservationList.isEmpty()) {
                // Hiển thị trạng thái trống nếu chưa đặt cuốn nào
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp), // Thêm size vào modifier ở đây nè bro!
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Bạn chưa đặt trước cuốn sách nào.", color = Color.Gray, fontSize = 15.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(bookViewModel.reservationList) { reservation ->
                        val statusText = reservation.status ?: "Đang giữ"
                        val (statusColor, statusBg) = when (statusText) {
                            "Đang giữ" -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)   // Xanh lá
                            "Hết hạn" -> Color(0xFFC62828) to Color(0xFFFFEBEE)    // Đỏ
                            "Bị hủy" -> Color(0xFF757575) to Color(0xFFF5F5F5)     // Xám
                            "Đã nhận sách" -> Color(0xFF1565C0) to Color(0xFFE3F2FD) // Xanh dương
                            else -> Color.DarkGray to Color.LightGray
                        }

                        // Định dạng lại ngày giờ hiển thị cho đẹp mắt
                        val formattedDate = remember(reservation.createdAt) {
                            try {
                                val parsed = LocalDateTime.parse(reservation.createdAt)
                                parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                            } catch (e: Exception) {
                                reservation.createdAt ?: "Chưa xác định"
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = reservation.bookTitle ?: "Tên sách",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Badge Trạng thái
                                    Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
                                        Text(
                                            text = statusText,
                                            color = statusColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Ngày đặt: $formattedDate", fontSize = 13.sp, color = Color.Gray)

                                // Nếu phiếu đang ở trạng thái "Đang giữ", hiển thị nút cho phép Sinh viên hủy đặt
                                if (statusText == "Đang giữ") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        OutlinedButton(
                                            onClick = {
                                                bookViewModel.cancelMyReservation(reservation.id, username)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
                                        ) {
                                            Text("Hủy đặt trước", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}