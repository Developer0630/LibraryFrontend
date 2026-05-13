package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * Cấu trúc phản hồi chuẩn từ Server
 * @param T Kiểu dữ liệu của đối tượng nằm trong trường 'data'
 * @property success Trạng thái yêu cầu (true: thành công, false: thất bại)
 * @property message Thông báo chi tiết từ backend (dùng để hiển thị Toast/Dialog)
 * @property data Dữ liệu trả về cụ thể (có thể là Object hoặc List)
 */
data class BaseResponse<T>(
    @SerializedName("success")  val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?
)
