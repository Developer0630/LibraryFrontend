package com.example.librarymobile.data.model.response

data class ReturnResponseDTO(
    val returnId: Long,
    val loanId: Long,
    val studentName: String,
    val bookTitle: String,
    val returnDate: String,
    val actualCondition: String,
    val lateFee: Double,
    val damageFee: Double,
    val totalFine: Double
)
