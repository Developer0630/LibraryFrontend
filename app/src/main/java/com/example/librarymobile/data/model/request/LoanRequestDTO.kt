package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

data class LoanRequestDTO(
    @SerializedName("studentId")
    val studentId: Long,

    @SerializedName("copyId")
    val copyId: Long,


    @SerializedName("reservationId")
    val reservationId: Long? = null, // Trống nếu mượn tự do

    @SerializedName("dueDate")
    val dueDate: String? = null,     // Định dạng chuỗi ISO hoặc để trống backend tự cộng 14 ngày

    @SerializedName("note")
    val note: String? = null
)