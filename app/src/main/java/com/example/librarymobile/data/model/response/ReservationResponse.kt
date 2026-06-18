package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class ReservationResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("bookTitle") val bookTitle: String?,
    @SerializedName("createdAt") val createdAt: String?, // Định dạng ISO LocalDateTime dạng chuỗi
    @SerializedName("status") val status: String?
)