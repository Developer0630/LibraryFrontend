package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

data class ReservationRequestDTO(
    @SerializedName("copy_id") val copy_id: Long
)