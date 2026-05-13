package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

/**
 * Dữ liệu gửi lên để cập nhật thông tin nhân sự
 * @property fullName Họ và tên mới
 * @property phone Số điện thoại mới
 * @property address Địa chỉ mới
 * @property roleId ID vai trò mới nếu có thay đổi chức vụ
 * @property status Trạng thái mới (ACTIVE, INACTIVE, LOCKED)
 */
data class UpdateStaffRequest(
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("phone_number") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("role_id") val roleId: Int?,
    @SerializedName("status") val status: String?
)
