package com.example.librarymobile.ui.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarymobile.ui.admin.book.BookViewModel
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.librarymobile.data.model.request.ReservationRequestDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    username: String,
    onNavigateToReservations: () -> Unit,
    onLogout: () -> Unit,
    bookViewModel: BookViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var messageAlert by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks(null)
    }

    LaunchedEffect(username) {
        bookViewModel.fetchBooks(null)
        bookViewModel.fetchMyReservations(username)
    }


    LaunchedEffect(bookViewModel.reservationMessage) {
        bookViewModel.reservationMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            bookViewModel.reservationMessage = null // Xóa bộ nhớ tạm tránh lặp Toast
            bookViewModel.fetchMyReservations(username)
            onNavigateToReservations() // Chuyển màn hình
        }
    }
    LaunchedEffect(bookViewModel.errorMessage) {
        bookViewModel.errorMessage?.let { error ->
            // In trực tiếp ra log của hệ thống
            android.util.Log.e("HUST_DEBUG", "Hiển thị Toast lỗi cho User: $error")
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            bookViewModel.errorMessage = null // Reset trạng thái lỗi
        }
    }

    val filteredBooks = bookViewModel.bookList.filter {
        val titleMatch = it.title?.contains(searchQuery, ignoreCase = true) ?: false
        val authorMatch = it.author?.contains(searchQuery, ignoreCase = true) ?: false
        titleMatch || authorMatch
    }

    val featuredBooks = filteredBooks.take(4)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "HUST BOOKHUB",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color(0xFF1A237E)
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToReservations) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Xem sách đã đặt trước",
                            tint = Color(0xFF1A237E)
                        )
                    }
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.background(Color(0xFF1A237E).copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Đăng xuất",
                            tint = Color(0xFFD32F2F)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F2F5))
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    WelcomeCard(username = username)
                }

                item {
                    SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it })
                }

                if (bookViewModel.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                        }
                    }
                }

                // KHU VỰC SÁCH GỢI Ý (Đã bọc kết nối gọi API thực tế)
                if (searchQuery.isEmpty() && featuredBooks.isNotEmpty() && !bookViewModel.isLoading) {
                    item {
                        SectionHeader(title = "Gợi ý cho bạn", icon = Icons.Default.AutoAwesome)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(featuredBooks) { book ->
                                val isAvailable = (book.totalStock ?: 0) > 0
                                FeaturedBookCard(
                                    title = book.title ?: "Không tiêu đề",
                                    author = book.author ?: "Ẩn danh",
                                    isAvailable = isAvailable,
                                    onActionClick = {
                                        if (isAvailable) {
                                        bookViewModel.createReservation(
                                            ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                            username
                                        )
                                        } else {
                                            bookViewModel.createReservation(
                                                ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                                username
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // KHU VỰC TẤT CẢ SÁCH (Đã bọc kết nối gọi API thực tế)
                if (!bookViewModel.isLoading) {
                    item {
                        SectionHeader(
                            title = if (searchQuery.isEmpty()) "Tất cả sách" else "Kết quả tìm kiếm (${filteredBooks.size})",
                            icon = Icons.Default.MenuBook
                        )
                    }

                    items(filteredBooks) { book ->
                        val stockCount = book.totalStock ?: 0
                        val isAvailable = stockCount > 0

                        BookRowItem(
                            title = book.title ?: "Không có tiêu đề",
                            author = book.author ?: "Ẩn danh",
                            category = book.category ?: "Chưa phân loại",
                            location = book.shelfLocation ?: "Kệ trống",
                            stock = stockCount,
                            isAvailable = isAvailable,
                            onOrderClick = {
                                if (isAvailable) {
                                    bookViewModel.createReservation(
                                        ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                        username
                                    )
                                } else {
                                    bookViewModel.createReservation(
                                        ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                        username
                                    )
                                }
                            }
                        )
                    }
                }
            }
            // Hộp thoại Dialog thông báo mượn tại quầy thông thường
            AnimatedVisibility(visible = messageAlert != null) {
                AlertDialog(
                    onDismissRequest = { messageAlert = null },
                    confirmButton = {
                        Button(
                            onClick = { messageAlert = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                        ) { Text("Đã hiểu") }
                    },
                    title = { Text("Trạng thái mượn sách", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E)) },
                    text = { Text(messageAlert ?: "") },
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }
    }
}

// --- CÁC COMPOSABLE CON GIỮ NGUYÊN HOÀN TOÀN CẤU TRÚC ĐẸP ĐẼ CỦA BRO ---
@Composable
fun WelcomeCard(username: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(colors = listOf(Color(0xFF1A237E), Color(0xFF283593))))
            .padding(24.dp)
    ) {
        Column {
            Text("Xin chào,", color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
            Text(username, color = Color.White, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(6.dp))
            Text("Hôm nay bạn muốn đặt lịch mượn cuốn sách nào?", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
        }
    }
}

@Composable
fun SearchBarSection(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Tìm tên sách, tác giả, thể loại...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF1A237E)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF1A237E),
            unfocusedBorderColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF1A237E))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black.copy(alpha = 0.8f))
    }
}

@Composable
fun FeaturedBookCard(title: String, author: String, isAvailable: Boolean, onActionClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).height(200.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A237E).copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFF1A237E))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = author, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Gray, fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isAvailable) "Còn sách" else "Hết sách",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
                IconButton(
                    onClick = onActionClick,
                    modifier = Modifier.size(32.dp).background(if (isAvailable) Color(0xFF1A237E) else Color(0xFFD32F2F), shape = RoundedCornerShape(10.dp))
                ) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Đặt sách", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BookRowItem(title: String, author: String, category: String, location: String, stock: Int, isAvailable: Boolean, onOrderClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(if (isAvailable) Color(0xFF1A237E).copy(alpha = 0.08f) else Color(0xFFD32F2F).copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "$stock", fontWeight = FontWeight.Black, color = if (isAvailable) Color(0xFF1A237E) else Color(0xFFD32F2F), fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Black.copy(alpha = 0.85f))
                Text(text = "Tác giả: $author", color = Color.Gray, fontSize = 12.sp)
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "• $category", fontSize = 11.sp, color = Color(0xFF1A237E).copy(alpha = 0.7f))
                    Text(text = "• Kệ: $location", fontSize = 11.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onOrderClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable) Color(0xFF1A237E) else Color(0xFFD32F2F)),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(text = if (isAvailable) "Đặt mượn" else "Đặt trước", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}