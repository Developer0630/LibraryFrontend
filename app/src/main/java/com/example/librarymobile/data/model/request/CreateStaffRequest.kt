package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

/**
 * Dữ liệu bắt buộc để tạo mới một tài khoản nhân viên/thủ thư
 * @property username Tên đăng nhập (Dùng để định danh, thường không được đổi sau khi tạo)
 * @property password Mật khẩu khởi tạo cho nhân viên
 * @property fullName Họ và tên đầy đủ
 * @property email Email liên lạc (Dùng để gửi cảnh báo hệ thống)
 * @property roleId ID vai trò (1: ADMIN, 2: LIBRARIAN...) dựa theo bảng Roles
 */
data class CreateStaffRequest(
    @SerializedName("username") val username: String,

    @SerializedName("password") val password: String,

    @SerializedName("full_name") val fullName: String,

    @SerializedName("email") val email: String,

    @SerializedName("role_id") val roleId: Int,

    @SerializedName("phone_number") val phone: String? = null,

    @SerializedName("address") val address: String? = null
)
