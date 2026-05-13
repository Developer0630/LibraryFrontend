package com.example.librarymobile.ui.admin.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Định nghĩa các chức năng trên Dashboard
data class DashboardItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val color: androidx.compose.ui.graphics.Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val menuItems = listOf(
        DashboardItem("Nhân sự", Icons.Default.Person, "staff_manage", MaterialTheme.colorScheme.primary),
        DashboardItem("Quy tắc", Icons.Default.Settings, "rules_manage", MaterialTheme.colorScheme.secondary),
//        DashboardItem("Sách", Icons.Default.MenuBook, "books_manage", androidx.compose.ui.graphics.Color(0xFF4CAF50)),
//        DashboardItem("Thống kê", Icons.Default.BarChart, "stats", androidx.compose.ui.graphics.Color(0xFFFF9800))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HUST ADMIN", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Logout logic */ }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Chào mừng quay trở lại,",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Quản trị viên",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Lưới các chức năng (Grid)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    AdminMenuItem(item)
                }
            }
        }
    }
}

@Composable
fun AdminMenuItem(item: DashboardItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { /* Điều hướng đến route của item */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = item.color
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}