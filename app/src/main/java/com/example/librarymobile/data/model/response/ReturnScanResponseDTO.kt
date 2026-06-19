package com.example.librarymobile.data.model.response

data class ReturnScanResponseDTO(
    val loanId: Long,
    val studentId: Long,
    val studentName: String,
    val bookTitle: String,
    val barcode: String?,
    val borrowDate: String, // Nhận chuỗi ISO từ LocalDateTime
    val dueDate: String,
    val overdueDays: Long,
    val overdueFine: Double,
    val status: String
)
