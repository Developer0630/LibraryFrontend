package com.example.librarymobile.ui.student

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.ReservationRequestDTO
import com.example.librarymobile.ui.admin.book.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentHomeScreen(
    username: String,
    onNavigateToReservations: () -> Unit,
    onLogout: () -> Unit,
    bookViewModel: BookViewModel = viewModel(),
    studentViewModel: StudentViewModel = viewModel {
        StudentViewModel(NetworkModule.studentApiService)
    }
) {
    var searchQuery by remember { mutableStateOf("") }
    var messageAlert by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Quản lý Tab hiện tại: 0 = Tra cứu sách, 1 = Tủ sách mượn, 2 = Hồ sơ & Phạt
    var selectedTab by remember { mutableStateOf(0) }

    // Tự động load dữ liệu ngay khi màn hình khởi tạo
    LaunchedEffect(Unit) {
        bookViewModel.fetchBooks(null)
        studentViewModel.loadStudentDashboardData()
    }

    LaunchedEffect(username) {
        bookViewModel.fetchBooks(null)
        bookViewModel.fetchMyReservations(username)
    }

    LaunchedEffect(bookViewModel.reservationMessage) {
        bookViewModel.reservationMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            bookViewModel.reservationMessage = null
            bookViewModel.fetchMyReservations(username)
            onNavigateToReservations()
        }
    }

    LaunchedEffect(bookViewModel.errorMessage) {
        bookViewModel.errorMessage?.let { error ->
            android.util.Log.e("HUST_DEBUG", "Hiển thị Toast lỗi cho User: $error")
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            bookViewModel.errorMessage = null
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
                        text = when (selectedTab) {
                            0 -> "HUST BOOKHUB"
                            1 -> "SÁCH CỦA BẠN"
                            else -> "HỒ SƠ SINH VIÊN"
                        },
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = Color(0xFF1A237E)
                    )
                },
                actions = {
                    if (selectedTab == 0) {
                        IconButton(onClick = onNavigateToReservations) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Xem sách đã đặt trước",
                                tint = Color(0xFF1A237E)
                            )
                        }
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
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("Tra cứu sách", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1A237E),
                        selectedTextColor = Color(0xFF1A237E),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.MenuBook, null) },
                    label = { Text("Tủ sách mượn", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1A237E),
                        selectedTextColor = Color(0xFF1A237E),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Hồ sơ & Phạt", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF1A237E),
                        selectedTextColor = Color(0xFF1A237E),
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF0F2F5))
        ) {
            when (selectedTab) {
                0 -> {
                    // TAB 0: TÌM KIẾM VÀ ĐẶT MƯỢN SÁCH CỦA BRO
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 32.dp)
                    ) {
                        item { WelcomeCard(username = username) }
                        item { SearchBarSection(query = searchQuery, onQueryChange = { searchQuery = it }) }

                        if (bookViewModel.isLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(color = Color(0xFF1A237E))
                                }
                            }
                        }

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
                                                bookViewModel.createReservation(
                                                    ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                                    username
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }

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
                                        bookViewModel.createReservation(
                                            ReservationRequestDTO(copy_id = (book.bookId ?: 0).toLong()),
                                            username
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: SÁCH ĐANG MƯỢN + LỊCH SỬ MƯỢN TRẢ + SÁCH ĐẶT TRƯỚC
                    StudentBorrowedTab(viewModel = studentViewModel)
                }
                2 -> {
                    // TAB 2: PROFILE + TỔNG TIỀN PHẠT + LỊCH SỬ ĐÓNG PHẠT + THÔNG BÁO
                    StudentProfileTab(viewModel = studentViewModel)
                }
            }

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

// --- CHI TIẾT CÁC COMPOSABLE TAB TRA CỨU ---

@Composable
fun StudentBorrowedTab(viewModel: StudentViewModel) {
    if (viewModel.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1A237E))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Sách Đang Mượn
            item {
                Text("Sách đang giữ trong tay", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
            if (viewModel.borrowedItemsState.isEmpty()) {
                item { Text("Bạn hiện không mượn cuốn sách nào.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp)) }
            } else {
                items(viewModel.borrowedItemsState) { item ->
                    // Khắc phục lỗi status hoặc dữ liệu bị null bằng cách dùng toán tử quy đổi an toàn
                    val isOverdue = item.status?.equals("Overdue", ignoreCase = true) ?: false
                    // Ép kiểu chuỗi an toàn, nếu chuỗi rỗng hoặc null sẽ không bị crash substring
                    val borrowDateSafe = item.borrowDate?.substringBefore("T") ?: "Chưa rõ"
                    val dueDateSafe = item.dueDate?.substringBefore("T") ?: "Chưa rõ"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.bookTitle ?: "Không có tiêu đề", fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Ngày mượn: $borrowDateSafe", fontSize = 12.sp, color = Color.Gray)
                                Text("Hạn trả: $dueDateSafe", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isOverdue) Color(0xFFD32F2F) else Color.Black)
                            }
                            Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isOverdue) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)).padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Text(if (isOverdue) "Quá hạn ⚠️" else "Đang mượn", color = if (isOverdue) Color(0xFFD32F2F) else Color(0xFF2E7D32), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Yêu Cầu Đặt Trước Hiện Có
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Yêu cầu đặt trước hiện có", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
            if (viewModel.reservationsState.isEmpty()) {
                item { Text("Không có yêu cầu đặt trước nào đang chờ.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp)) }
            } else {
                items(viewModel.reservationsState) { res ->
                    val reservedAtSafe = res.reservedAt?.substringBefore("T") ?: "Chưa rõ"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(res.bookTitle ?: "Không có tiêu đề", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("Đặt ngày: $reservedAtSafe", fontSize = 12.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(res.status ?: "PENDING", fontWeight = FontWeight.Black, color = Color(0xFF1A237E), fontSize = 12.sp)
                        }
                    }
                }
            }

            // 3. Lịch Sử Mượn Trả
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Lịch sử mượn trả trước đây", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
            if (viewModel.borrowHistoryState.isEmpty()) {
                item { Text("Chưa có lịch sử giao dịch mượn trả sách.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp)) }
            } else {
                items(viewModel.borrowHistoryState) { history ->
                    val returnedAtSafe = history.returnedAt?.substringBefore("T") ?: "Chưa rõ"
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(history.bookTitle ?: "Không có tiêu đề", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Đã trả: $returnedAtSafe", fontSize = 12.sp, color = Color.Gray)
                                Text("Trạng thái: ${history.returnStatus ?: "Nguyên vẹn"}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentProfileTab(viewModel: StudentViewModel) {
    val profile = viewModel.profileState
    val finesSummary = viewModel.fineSummaryState

    if (profile == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1A237E))
        }
    } else {
        val unpaidAmount = finesSummary?.unpaidFine ?: profile.totalDebt
        val hasFine = unpaidAmount > 0

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Thẻ Hồ Sơ Sinh Viên
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(50.dp).background(Color(0xFF1A237E).copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, null, tint = Color(0xFF1A237E), modifier = Modifier.size(26.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(profile.fullName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Mã sinh viên: ${profile.studentCode}", color = Color.Gray, fontSize = 13.sp)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color(0xFFF0F2F5))
                        Text("Lớp học: ${profile.clazz}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Chuyên ngành: ${profile.major}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Email liên hệ: ${profile.email}", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            // 2. Tổng Tiền Phạt Chưa Thanh Toán
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = if (hasFine) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Tổng công nợ phí phạt chưa nộp", fontSize = 13.sp, color = if (hasFine) Color(0xFFC62828) else Color(0xFF2E7D32))
                            Text("$unpaidAmount VNĐ", fontWeight = FontWeight.Black, fontSize = 22.sp, color = if (hasFine) Color(0xFFD32F2F) else Color(0xFF2E7D32))
                        }
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = if (hasFine) Color(0xFFD32F2F) else Color(0xFF2E7D32), modifier = Modifier.size(28.dp))
                    }
                }
            }

            // 3. Nhật Ký Lỗi Vi Phạm
            item { Text("Nhật ký lỗi vi phạm", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            if (viewModel.violationsState.isEmpty()) {
                item { Text("Bạn chấp hành nội quy rất tốt, không có vi phạm! ✨", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(viewModel.violationsState) { violation ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(violation.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Ngày vi phạm: ${violation.createdAt.substringBefore("T")}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("-${violation.fineAmount} đ", fontWeight = FontWeight.Black, color = Color(0xFFD32F2F), fontSize = 14.sp)
                        }
                    }
                }
            }

            // 4. Lịch Sử Thanh Toán/Đóng Phạt
            item { Text("Lịch sử đóng tiền phạt", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            if (viewModel.finePaymentsState.isEmpty()) {
                item { Text("Chưa có lịch sử nộp tiền phạt.", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(viewModel.finePaymentsState) { payment ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Hình thức: ${payment.paymentMethod}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                Text("Ngày đóng: ${payment.paymentDate.substringBefore("T")}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("+${payment.amount} đ", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 14.sp)
                        }
                    }
                }
            }

            // 5. Thông Báo Hệ Thống
            item { Text("Thông báo từ thư viện", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            if (viewModel.notificationsState.isEmpty()) {
                item { Text("Không có thông báo mới nào.", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(viewModel.notificationsState) { noti ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = if (noti.isRead) Color.White.copy(alpha = 0.6f) else Color(0xFFE8F5E9)), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(noti.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A237E))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(noti.content, fontSize = 13.sp, color = Color.Black.copy(alpha = 0.8f))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(noti.createdAt.substringBefore("T"), fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

// --- CÁC COMPOSABLE WIDGETS GIAO DIỆN TÌM KIẾM CŨ GIỮ NGUYÊN ---

@Composable
fun WelcomeCard(username: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(16.dp).clip(RoundedCornerShape(28.dp)).background(Brush.horizontalGradient(colors = listOf(Color(0xFF1A237E), Color(0xFF283593)))).padding(24.dp)) {
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
        value = query, onValueChange = onQueryChange,
        placeholder = { Text("Tìm tên sách, tác giả, thể loại...", color = Color.Gray) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF1A237E)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedBorderColor = Color(0xFF1A237E), unfocusedBorderColor = Color.Transparent),
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF1A237E))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold), color = Color.Black.copy(alpha = 0.8f))
    }
}

@Composable
fun FeaturedBookCard(title: String, author: String, isAvailable: Boolean, onActionClick: () -> Unit) {
    Card(modifier = Modifier.width(160.dp).height(200.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Column {
                Box(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A237E).copy(alpha = 0.05f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Bookmark, null, tint = Color(0xFF1A237E))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = author, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.Gray, fontSize = 12.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = if (isAvailable) "Còn sách" else "Hết sách", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isAvailable) Color(0xFF2E7D32) else Color(0xFFD32F2F))
                IconButton(onClick = onActionClick, modifier = Modifier.size(32.dp).background(if (isAvailable) Color(0xFF1A237E) else Color(0xFFD32F2F), shape = RoundedCornerShape(10.dp))) {
                    Icon(Icons.Default.ShoppingBag, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun BookRowItem(title: String, author: String, category: String, location: String, stock: Int, isAvailable: Boolean, onOrderClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 5.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(if (isAvailable) Color(0xFF1A237E).copy(alpha = 0.08f) else Color(0xFFD32F2F).copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
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
            Button(onClick = onOrderClick, shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isAvailable) Color(0xFF1A237E) else Color(0xFFD32F2F)), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp), modifier = Modifier.height(36.dp)) {
                Text(text = if (isAvailable) "Đặt mượn" else "Đặt trước", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}