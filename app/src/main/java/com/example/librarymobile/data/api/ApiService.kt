package com.example.librarymobile.data.api

import com.example.librarymobile.data.model.request.CreateStaffRequest
import com.example.librarymobile.data.model.request.LoginRequest
import com.example.librarymobile.data.model.request.UpdateStaffRequest
import com.example.librarymobile.data.model.response.BaseResponse
import com.example.librarymobile.data.model.response.RoleResponse
import com.example.librarymobile.data.model.response.StaffResponse
import com.example.librarymobile.data.model.response.SystemRuleResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Danh sách các Endpoints kết nối với Backend Spring Boot
 */
interface ApiService {
    /**
     * Đăng nhập vào hệ thống
     * @param request gọi tới LoginRequest chứa "username" và "password"
     * @return Thông tin User và Token nếu thành công
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<Map<String,Any>>

    @POST("api/auth/register")
    suspend fun register(@Body staffData: CreateStaffRequest): BaseResponse<StaffResponse>
    /**
     * Lấy danh sách toàn bộ vai trò (Admin, Librarian, Member...)
     */
    // --- QUẢN LÝ VAI TRÒ (ROLES) ---
    @GET("api/roles")
    suspend fun getRoles(): BaseResponse<List<RoleResponse>>

    @POST("api/roles")
    suspend fun createRole(@Body roleRequest: Map<String, Any>): BaseResponse<RoleResponse>

    @PUT("api/roles/{id}")
    suspend fun updateRole(@Path("id") id: Int, @Body roleRequest: Map<String, Any>): BaseResponse<RoleResponse>

    // --- QUẢN LÝ NHÂN SỰ (STAFF) ---

    /**
     * Lấy danh sách toàn bộ nhân viên/thủ thư trong hệ thống
     */
    @GET("api/staff")
    suspend fun getStaffs(): BaseResponse<List<StaffResponse>>

    /**
     * Tạo mới một nhân viên/thủ thư
     * @param staffRequest Dữ liệu nhân viên mới (Map hoặc class StaffRequest)
     */
    @POST("api/staff")
    suspend fun createStaff(
        @Body staffRequest: CreateStaffRequest
    ): BaseResponse<StaffResponse>

    /**
     * Cập nhật thông tin nhân viên theo ID
     * @param id ID của nhân viên cần sửa (lấy từ StaffResponse.userId)
     * @param request Đối tượng chứa các thông tin thay đổi
     */
    @PUT("api/staff/{id}")
    suspend fun updateStaff(
        @Path("id") id: Int,
        @Body request: UpdateStaffRequest
    ): BaseResponse<StaffResponse>

    /**
     * Xóa nhân viên khỏi hệ thống
     * @param id ID của nhân viên cần xóa
     * @return Trả về BaseResponse với success = true nếu xóa thành công
     */
    @DELETE("api/staff/{id}")
    suspend fun deleteStaff(
        @Path("id") id: Int
    ): BaseResponse<Unit> // Unit tương đương với 'void' trong Java vì xóa thường không trả về data

    /**
     * Lấy danh sách tất cả quy tắc hệ thống (Hạn mượn, phí phạt, phí hư hỏng...)
     */
    @GET("api/system-rules")
    suspend fun getSystemRules(): BaseResponse<List<SystemRuleResponse>>

    /**
     * Cập nhật giá trị cho một quy tắc cụ thể
     * @param key Mã quy tắc cần sửa (ví dụ: 'OVERDUE_FINE')
     * @param body Map chứa giá trị mới, ví dụ: {"value": "10000"}
     */
    @PUT("api/system-rules/{key}")
    suspend fun updateSystemRule(
        @Path("key") key: String,
        @Body body: Map<String, String>
    ): BaseResponse<SystemRuleResponse>
}