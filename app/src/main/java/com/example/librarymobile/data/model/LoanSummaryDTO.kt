package com.example.librarymobile.data.model

data class LoanSummaryDTO(
    val totalLoans: Int,
    val totalReturns: Int,
    val onTimeRate: Double,
    val overdueCount: Int,
    val totalRevenue: Double
)
