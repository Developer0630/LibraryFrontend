package com.example.librarymobile.data.model

data class TopBorrowedBookDTO(
    val bookId: Long,
    val title: String,
    val author: String,
    val borrowCount: Int
)
