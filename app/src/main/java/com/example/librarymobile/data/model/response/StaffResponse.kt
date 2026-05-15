package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName
/**
 * Thông tin chi tiết của nhân viên/thủ thư
 * Ánh xạ từ bảng 'User' và 'Staff' trong Database
 */
data class StaffResponse(
    @SerializedName("staffId") val staffId: Long?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("positionName") val positionName: String?
)
