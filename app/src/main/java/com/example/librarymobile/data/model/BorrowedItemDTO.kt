package com.example.librarymobile.data.model

// Dùng cho API xem sách đang mượn hiện tại
data class BorrowedItemDTO(
    val loanId: Long,
    val bookTitle: String,
    val borrowDate: String,  // Nhận dạng chuỗi ISO từ LocalDateTime (e.g., "2026-06-19T09:35:25")
    val dueDate: String,
    val status: String       // Active, Overdue, Sắp đến hạn...
)

