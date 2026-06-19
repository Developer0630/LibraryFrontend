package com.example.librarymobile.data.model

data class FineSummaryDTO(
    val totalFine: Double,
    val unpaidFine: Double,
    val details: List<ViolationDTO>
)
