package com.example.librarymobile.data.model.request

data class BookRequest(
    val title: String,
    val author: String,
    val isbn: String,
    val category: String,
    val description: String,
    val initialStock: Int,
    val shelfLocation: String
)

data class UpdateStockRequest(
    val adjustment: Int
)