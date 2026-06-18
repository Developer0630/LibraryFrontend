package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class EligibilityResponseDTO(
    @SerializedName("pass")
    val pass: Boolean,

    @SerializedName("reason")
    val reason: String
)