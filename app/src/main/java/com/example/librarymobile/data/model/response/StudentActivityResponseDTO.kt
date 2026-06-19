package com.example.librarymobile.data.model.response

data class StudentActivityResponseDTO(
    val department: String,          // Map với s.major (Backend trả về vị trí tham số 1)
    val className: String,           // Map với s.clazz (Backend trả về vị trí tham số 2)
    val activeStudentsCount: Long,   // Số sinh viên tham gia mượn sách
    val totalBorrows: Long           // Tổng số lượt mượn của lớp
)
