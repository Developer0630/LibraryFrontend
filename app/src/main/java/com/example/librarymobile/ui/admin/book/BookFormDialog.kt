package com.example.librarymobile.ui.admin.book

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.librarymobile.data.model.request.BookRequest
import com.example.librarymobile.data.model.response.BookResponse

@Composable
fun BookFormDialog(
    book: BookResponse? = null,
    onDismiss: () -> Unit,
    onConfirm: (BookRequest) -> Unit
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var author by remember { mutableStateOf(book?.author ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var category by remember { mutableStateOf(book?.category ?: "") } // Đại diện cho genre
    var shelfLocation by remember { mutableStateOf(book?.shelfLocation ?: "") }
    var price by remember { mutableStateOf(book?.description ?: "") } // Tạm thời map từ state hoặc để trống vì BookResponse hôm trước chưa có price
    var description by remember { mutableStateOf(book?.description ?: "") }
    var initialStock by remember { mutableStateOf("1") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (book == null) "Thêm Đầu Sách Mới" else "Cập Nhật Thông Tin") },
        text = {
            // Thêm Scroll phòng trường hợp màn hình điện thoại nhỏ nhập nhiều ô bị tràn UI
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tựa sách *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = author, onValueChange = { author = it }, label = { Text("Tác giả *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = isbn, onValueChange = { isbn = it }, label = { Text("Mã ISBN *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Thể loại (Genre)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = shelfLocation, onValueChange = { shelfLocation = it }, label = { Text("Mã kệ sách (Vd: SHELF-A1)") }, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Giá tiền (VNĐ) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Mô tả sách") }, modifier = Modifier.fillMaxWidth(), maxLines = 3)

                // Chỉ hiển thị số lượng khi THÊM MỚI (vì sửa sách sẽ dùng tính năng điều chỉnh stock riêng)
                if (book == null) {
                    OutlinedTextField(
                        value = initialStock,
                        onValueChange = { initialStock = it },
                        label = { Text("Số lượng nhập kho ban đầu") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && author.isNotBlank() && isbn.isNotBlank() && price.isNotBlank()) {
                        onConfirm(
                            BookRequest(
                                title = title,
                                author = author,
                                isbn = isbn,
                                category = category,
                                description = description,
                                initialStock = if (book == null) (initialStock.toIntOrNull() ?: 1) else 0,
                                shelfLocation = shelfLocation,
                                price = price.toDoubleOrNull() ?: 0.0
                            )
                        )
                    }
                }
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}