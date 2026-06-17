package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("userId") val userId: Long,
    @SerializedName("username") val username: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("roles") val roles: Set<String>, // Nhận về mảng quyền ["ADMIN", "STAFF", "STUDENT"]
    @SerializedName("message") val message: String
)
