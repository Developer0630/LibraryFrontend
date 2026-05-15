package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

data class StaffRequest(
    @SerializedName("staffId") val staffId: Long? = null,

    @SerializedName("position") val position: PositionRequest? = null,

    @SerializedName("user") val user: UserRequest? = null
)

data class PositionRequest(@SerializedName("positionId") val positionId: Int)
data class UserRequest(@SerializedName("fullName") val fullName: String)