package com.example.librarymobile.data.model.response

data class LoanSummaryResponseDTO(
    val totalLoans: Long,
    val totalReturns: Long,
    val onTimeReturns: Long,
    val overdueReturns: Long,
    val onTimeRate: Double
)
