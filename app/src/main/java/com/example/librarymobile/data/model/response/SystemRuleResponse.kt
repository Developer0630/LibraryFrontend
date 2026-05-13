package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

/**
 * Model chứa các thông số cấu hình hệ thống
 * @property ruleKey Mã định danh quy tắc (Ví dụ: BORROW_TIME_LIMIT)
 * @property ruleValue Giá trị cấu hình (Ví dụ: 120)
 * @property description Mô tả chi tiết bằng tiếng Việt để hiển thị lên UI
 * @property unit Đơn vị tính (ngày, VNĐ, cuốn, %)
 */
data class SystemRuleResponse(
    @SerializedName("rule_key") val ruleKey: String,

    @SerializedName("rule_value") val ruleValue: String,

    @SerializedName("description") val description: String,

    @SerializedName("unit") val unit: String?
)
