package com.example.librarymobile.ui.admin.book

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarymobile.data.model.response.BookResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookManageScreen(viewModel: BookViewModel = viewModel(), onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }

    // Quản lý trạng thái đóng/mở Dialog
    var showDialog by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<BookResponse?>(null) }

    LaunchedEffect(searchQuery) {
        viewModel.fetchBooks(searchQuery)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFFF8F9FA))) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Library Stock",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF2D3142),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search by title, author, ISBN...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(15.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF5C6BC0),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    selectedBook = null // Set null để biết là đang muốn THÊM MỚI
                    showDialog = true
                },
                containerColor = Color(0xFF5C6BC0),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Book")
            }
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        if (viewModel.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF5C6BC0))
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(viewModel.bookList) { book ->
                    BookItem(
                        book = book,
                        onDelete = { viewModel.deleteBook(book.bookId!!) },
                        onEdit = {
                            selectedBook = book // Gán book được chọn vào đây để SỬA
                            showDialog = true
                        },
                        onClick = { /* Lần tới làm: Chuyển sang màn hình chi tiết bản in */ }
                    )
                }
            }
        }
    }

    // Hiển thị Dialog xử lý dữ liệu
    if (showDialog) {
        BookFormDialog(
            book = selectedBook,
            onDismiss = { showDialog = false },
            onConfirm = { request ->
                if (selectedBook == null) {
                    viewModel.createBook(request)
                } else {
                    viewModel.updateBook(selectedBook!!.bookId!!, request)
                }
                showDialog = false // Đóng dialog sau khi bấm lưu
            }
        )
    }
}
@Composable
fun BookItem(
    book: BookResponse,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top // Chuyển sang Top để căn chỉnh mô tả dài tốt hơn
            ) {
                // Khối Bìa Sách Giả Lập thiết kế lại đổ màu Gradient sang chảnh
                Box(
                    modifier = Modifier
                        .size(60.dp, 84.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFEEF2F6), Color(0xE0E2E8F0))
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = Color(0xFF6366F1)
                    )
                }

                // Cột hiển thị thông tin chính (Khớp hoàn toàn với JSON thực tế)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    // Tiêu đề sách
                    Text(
                        text = book.title ?: "Chưa có tiêu đề",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    // Tác giả
                    Text(
                        text = "Tác giả: ${book.author ?: "Không rõ"}",
                        color = Color(0xFF6B7280),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Mô tả sách (Lấy từ field description của JSON)
                    Text(
                        text = book.description ?: "Không có mô tả cho cuốn sách này.",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp,
                        maxLines = 2, // Hiển thị tối đa 2 dòng mô tả ngắn
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hiển thị Giá tiền (Vì các trường shelfLocation, totalStock đang null nên ẩn đi, thay bằng Badge Giá)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Color(0xFFEEF2F6),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "ID: ${book.bookId ?: 0}",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = Color(0xFF4B5563),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Badge Giá tiền cực nổi bật
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                // Ở bài trước BookResponse chưa có price,
                                // Bro nhớ kiểm tra lại file BookResponse.kt xem đã khai báo "val price: Double?" chưa nhé!
                                text = formatVnd(book.price),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 12.sp,
                                color = Color(0xFF065F46),
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // Thanh gạch phân cách mỏng tinh tế
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 0.5.dp,
                color = Color(0xFFE5E7EB)
            )

            // Hàng Action Buttons dọn xuống dưới giúp bấm cực kỳ thoải mái
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.size(36.dp)
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
fun formatVnd(price: Double?): String {
    if (price == null) return "0 đ"
    return java.text.NumberFormat.getIntegerInstance(java.util.Locale("vi", "VN")).format(price) + " đ"
}