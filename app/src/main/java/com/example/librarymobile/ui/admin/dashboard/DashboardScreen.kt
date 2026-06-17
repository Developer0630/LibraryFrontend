package com.example.librarymobile.ui.admin.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun DashboardScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFFF0F2F5))))
            .verticalScroll(scrollState)
    ) {
        // --- PHẦN 1: HEADER & PROFILE ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 32.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Bảng điều khiển", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("Chào Admin !", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }

            // Cụm nút chức năng hệ thống góc phải
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Nút thông báo
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                }

                // Nút Đăng xuất tiện lợi
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Đăng xuất",
                        tint = Color.White
                    )
                }
            }
        }

        // --- PHẦN 2: GLASS CARD TỔNG QUAN ---
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(180.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawCircle(color = Color(0xFFD32F2F).copy(alpha = 0.05f), radius = 200f, center = center)
                }
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Hiệu suất thư viện", fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("85%", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
                        Text(" mục tiêu tháng", modifier = Modifier.padding(bottom = 12.dp, start = 8.dp), color = Color.Gray)
                    }
                    LinearProgressIndicator(
                        progress = 0.85f,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                        color = Color(0xFF1A237E), // Đồng bộ thanh progress sang màu Indigo cho đồng nhất
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- PHẦN 3: QUICK ACTIONS ---
        Text("Thao tác nhanh", modifier = Modifier.padding(horizontal = 24.dp), fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { QuickActionItem("Nhân viên", Icons.Default.PersonAdd, Color(0xFF4285F4)) { navController.navigate("staff_manage") } }
            item { QuickActionItem("Thêm sách", Icons.Default.AddBusiness, Color(0xFF34A853)) { navController.navigate("book_manage") } }
            item { QuickActionItem("Báo cáo", Icons.Default.PieChart, Color(0xFFFBBC05)) { /* Action */ } }
            item { QuickActionItem("Cài đặt", Icons.Default.Settings, Color(0xFF7B1FA2)) { /* Action */ } }
        }

        // --- PHẦN 4: RECENT ACTIVITY ---
        Surface(
            modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                Text("Hoạt động gần đây", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                ActivityItem("SV001 vừa mượn 'Lập trình Kotlin'", "10 phút trước")
                ActivityItem("Nhân viên A cập nhật hệ thống", "1 giờ trước")
                ActivityItem("Sách 'AI' được trả bởi SV005", "3 giờ trước")
            }
        }
    }
}

@Composable
fun QuickActionItem(title: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Surface(modifier = Modifier.size(70.dp), shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.1f)) {
            Icon(icon, null, tint = color, modifier = Modifier.padding(20.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ActivityItem(text: String, time: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(Color(0xFFD32F2F), CircleShape))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text, fontSize = 14.sp)
            Text(time, fontSize = 12.sp, color = Color.Gray)
        }
    }
}