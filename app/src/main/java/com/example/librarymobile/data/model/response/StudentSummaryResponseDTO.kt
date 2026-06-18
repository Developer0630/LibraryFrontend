package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class StudentSummaryResponseDTO(
    @SerializedName("id")
    val id: Long,

    @SerializedName("studentCode")
    val studentCode: String,

    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("pendingReservations")
    val pendingReservations: Long,

    @SerializedName("activeLoans")
    val activeLoans: Long,

    @SerializedName("totalDebt")
    val totalDebt: Double,

    @SerializedName("remainingLimit")
    val remainingLimit: Long,

    @SerializedName("status")
    val status: String // ACTIVE hoặc LOCKED
)