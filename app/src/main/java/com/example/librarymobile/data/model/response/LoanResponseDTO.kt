package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class LoanResponseDTO(
    @SerializedName("loanId")
    val loanId: Long,

    @SerializedName("studentName")
    val studentName: String,

    @SerializedName("studentCode")
    val studentCode: String,

    @SerializedName("bookTitle")
    val bookTitle: String,

    @SerializedName("barcode")
    val barcode: String?,

    @SerializedName("borrowDate")
    val borrowDate: Any?, // Đổi sang Any? để tránh lỗi crash do Gson không parse được Date

    @SerializedName("dueDate")
    val dueDate: Any?,    // Đổi sang Any?

    @SerializedName("returnedAt")
    val returnedAt: Any?, // Đổi sang Any?

    @SerializedName("status")
    val status: String,

    @SerializedName("returnStatus")
    val returnStatus: String?,

    @SerializedName("staffNote")
    val staffNote: String?
)