package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName
/**
 * Thông tin chi tiết của nhân viên/thủ thư
 * Ánh xạ từ bảng 'User' và 'Staff' trong Database
 */
data class StaffResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_number") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("date_of_birth") val dob: String?,
    @SerializedName("status") val status: String, // ACTIVE, INACTIVE, LOCKED
    @SerializedName("role") val role: RoleResponse?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String?
)
