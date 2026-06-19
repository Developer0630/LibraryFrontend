package com.example.librarymobile.data.model

data class ReservationDTO(
    val reservationId: Long,
    val bookTitle: String,
    val reservedAt: String,
    val status: String
)
