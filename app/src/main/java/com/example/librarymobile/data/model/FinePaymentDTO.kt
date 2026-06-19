package com.example.librarymobile.data.model

data class FinePaymentDTO(
    val paymentId: Long,
    val amount: Double,
    val paymentDate: String,
    val paymentMethod: String
)
