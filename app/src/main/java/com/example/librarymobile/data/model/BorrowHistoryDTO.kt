package com.example.librarymobile.data.model
// Dùng cho API lịch sử mượn trả đầy đủ
data class BorrowHistoryDTO(
    val loanId: Long,
    val bookTitle: String,
    val borrowDate: String,
    val returnedAt: String?,
    val returnStatus: String?, // Nguyên vẹn, Hư hỏng, Mất sách
    val staffNote: String?
)
