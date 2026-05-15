package com.example.librarymobile.ui.admin.staff

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarymobile.data.api.NetworkModule
import com.example.librarymobile.data.model.request.PositionRequest
import com.example.librarymobile.data.model.request.StaffRequest
import com.example.librarymobile.data.model.request.UserRequest
import com.example.librarymobile.data.model.response.StaffResponse
import kotlinx.coroutines.launch

class StaffViewModel : ViewModel() {
    var staffList = mutableStateListOf<StaffResponse>()
        private set

    var isLoading by mutableStateOf(false)
    var errorMsg by mutableStateOf<String?>(null)

    fun fetchStaff() {
        viewModelScope.launch {
            isLoading = true
            errorMsg = null
            try {
                val response = NetworkModule.apiService.getAllStaffs()

                if (response.isSuccessful) { // Kiểm tra HTTP Status 200
                    val data = response.body() // Lấy dữ liệu mảng trực tiếp
                    Log.d("API_CHECK", "Dữ liệu đổ về: $data")

                    staffList.clear()
                    data?.let { staffList.addAll(it) }
                } else {
                    errorMsg = "Lỗi từ Server: ${response.code()}"
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Lỗi kết nối: ${e.message}")
                errorMsg = "Không thể tải danh sách nhân viên"
            } finally {
                isLoading = false
            }
        }
    }
    fun addStaff(id: Long, name: String, posId: Int) {
        viewModelScope.launch {
            try {
                val request = StaffRequest(staffId = id, position = PositionRequest(posId))
                val response = NetworkModule.apiService.createStaff(request)
                if (response.isSuccessful) {fetchStaff()}
                else {
                    // IN LỖI NÀY RA LOGCAT ĐỂ XEM
                    Log.e("CRUD_ERROR", "Thêm thất bại: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) { Log.e("API", "Add Error") }
        }
    }

    fun editStaff( id: Long, name: String, posId: Int) {
        viewModelScope.launch {
            try {
                val request = StaffRequest(
                    staffId = id,
                    user = UserRequest(name),
                    position = PositionRequest(posId)
                )
                val response = NetworkModule.apiService.updateStaff(id, request)
                if (response.isSuccessful) {fetchStaff()}
                else {
                    Log.e("CRUD_ERROR", "Sửa thất bại: ${response.code()} - ${response.errorBody()?.string()}")
                    Log.e("CRUD_ERROR", "Request: $request")
                }
            } catch (e: Exception) { Log.e("API", "Edit Error") }
        }
    }

    fun deleteStaff(id: Long) {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.deleteStaff(id)
                if (response.isSuccessful) {
                    staffList.removeIf { it.staffId == id }
                }
            } catch (e: Exception) { Log.e("API", "Delete Error") }
        }
    }
}