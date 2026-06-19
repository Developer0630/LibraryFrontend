package com.example.librarymobile.data.model

data class ViolationDTO(
    val incidentId: Long,
    val title: String,       // Tên vi phạm (e.g., Trả trễ hạn, Làm rách sách)
    val fineAmount: Double,
    val createdAt: String,
    val status: String
)
