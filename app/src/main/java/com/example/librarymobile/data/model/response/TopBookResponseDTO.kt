package com.example.librarymobile.data.model.response

data class TopBookResponseDTO(
    val bookId: Long,
    val title: String,
    val author: String,
    val borrowCount: Long
)
