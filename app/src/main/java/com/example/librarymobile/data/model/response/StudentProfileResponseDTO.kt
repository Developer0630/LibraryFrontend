package com.example.librarymobile.data.model.response

data class StudentProfileResponseDTO(
    val studentCode: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String?,
    val major: String,       // Khớp với trường 'major' ở Backend
    val clazz: String,       // Khớp với trường 'clazz' ở Backend
    val totalDebt: Double
)
