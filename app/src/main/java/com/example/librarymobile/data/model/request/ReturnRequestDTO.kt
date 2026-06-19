package com.example.librarymobile.data.model.request

data class ReturnRequestDTO(
    val loanId: Long,
    val conditionStatus: String, // NORMAL, DAMAGED_LIGHT, DAMAGED_MEDIUM, DAMAGED_HEAVY, LOST
    val damageFee: Double?,
    val staffNote: String
)
