package com.example.librarymobile.ui.admin.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarymobile.data.model.response.StaffResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManageScreen(viewModel: StaffViewModel = viewModel(), onBack: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedStaff by remember { mutableStateOf<StaffResponse?>(null) }

    LaunchedEffect(Unit) { viewModel.fetchStaff() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "QUẢN LÝ NHÂN SỰ",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp) // Dùng Modifier để chỉnh size
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { selectedStaff = null; showDialog = true },
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Thêm mới") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F9FA))) {
            if (viewModel.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFFD32F2F))
            }

            LazyColumn(
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(viewModel.staffList) { staff ->
                    StaffItem(
                        staff = staff,
                        onEdit = { selectedStaff = staff; showDialog = true },
                        onDelete = { viewModel.deleteStaff(staff.staffId!!) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        StaffFormDialog(
            staff = selectedStaff,
            onDismiss = { showDialog = false },
            onConfirm = { id, name, pId ->
                // Logic API giữ nguyên như cũ
                if (selectedStaff == null) viewModel.addStaff(id, name, pId)
                else viewModel.editStaff(selectedStaff!!.staffId!!, name, pId)
                showDialog = false
            }
        )
    }
}

@Composable
fun StaffItem(staff: StaffResponse, onEdit: () -> Unit, onDelete: () -> Unit) {
    // Màu sắc ngẫu nhiên cho Avatar dựa trên ID
    val avatarColor = remember(staff.staffId) {
        listOf(Color(0xFF5C6BC0), Color(0xFF66BB6A), Color(0xFFFFA726), Color(0xFFEF5350)).random()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar với chữ cái đầu của tên
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(avatarColor.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = staff.fullName?.take(1)?.uppercase() ?: "?",
                    color = avatarColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = staff.fullName ?: "N/A",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFFF1F3F4),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = staff.positionName ?: "Nhân viên",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray
                    )
                }
            }

            // Action Buttons
            Row {
                FilledIconButton(
                    onClick = onEdit,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFE8EAF6)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color(0xFF3F51B5), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                FilledIconButton(
                    onClick = onDelete,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xFFFFEBEE)),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFD32F2F), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffFormDialog(
    staff: StaffResponse? = null,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, Int) -> Unit
) {
    var idText by remember { mutableStateOf(staff?.staffId?.toString() ?: "") }
    var nameText by remember { mutableStateOf(staff?.fullName ?: "") }
    var posId by remember { mutableStateOf(1) }
    val positions = listOf("Thủ thư" to 1, "Quản lý" to 2, "Kỹ thuật" to 3)
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        containerColor = Color.White,
        title = {
            Text(
                if (staff == null) "Thêm nhân viên" else "Cập nhật",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(top = 8.dp)) {
                OutlinedTextField(
                    value = idText,
                    onValueChange = { idText = it },
                    label = { Text("Mã User ID") },
                    enabled = staff == null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Fingerprint, null) }
                )
                OutlinedTextField(
                    value = nameText,
                    onValueChange = { nameText = it },
                    label = { Text("Họ và tên") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Badge, null) }
                )

                // Dropdown chọn chức vụ (Nâng cấp từ nhập số sang chọn)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = positions.find { it.second == posId }?.first ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chức vụ") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        positions.forEach { pos ->
                            DropdownMenuItem(
                                text = { Text(pos.first) },
                                onClick = {
                                    posId = pos.second
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(idText.toLongOrNull() ?: 0L, nameText, posId) },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Xác nhận") }
        }
    )
}