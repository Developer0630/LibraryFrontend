package com.example.librarymobile.data.model.response



import com.google.gson.annotations.SerializedName



data class RoleResponse(
    @SerializedName("role_id") val roleId: Long?,
    @SerializedName("role_name") val roleName: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("permissions") val permissions: List<PermissionResponse>?
)

data class PermissionResponse(
    @SerializedName("permission_id") val permissionId: Long?,
    @SerializedName("permission_name") val permissionName: String?,
    @SerializedName("module") val module: String?
)


/**
 *  annotation @SerializedName dùng để mapping giữa tên biến trong class
và tên trường trong JSON mà backend trả về
 *  tại sao lại dùng ở đây:
 *  1: đó là vì nếu cách đặt tên biến khác nhau thì sẽ gây hệ thống khó hiểu
vì thường nếu kh dùng annotation này nó sẽ tìm đến tến biến giống hệt với tên trường trong json
từ đó dẫn đên null pointer exception
ví dụ
Backend (Spring Boot/Database): Thường dùng kiểu snake_case (ví dụ: full_name, user_id, created_at).
Frontend (Kotlin/Java): Quy tắc chuẩn là dùng camelCase (ví dụ: fullName, userId, createdAt)
Nếu nó không thấy fullName (vì backend gửi về full_name), biến đó sẽ bị null
 *  2: Nếu có sự thay đổi tên biến trong tương lai thì chỉ cần thay đổi ở đây vì nó đã được bảo vệ
 *  3. Việc public lên store sau này...
 */