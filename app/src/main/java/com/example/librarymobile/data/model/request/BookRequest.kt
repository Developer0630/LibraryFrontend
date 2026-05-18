package com.example.librarymobile.data.model.request

import com.google.gson.annotations.SerializedName

data class BookRequest(
    @SerializedName("title") val title: String,
    @SerializedName("author") val author: String,
    @SerializedName("isbn") val isbn: String,
    @SerializedName("genre") val category: String,
    @SerializedName("description") val description: String,
    @SerializedName("quantity") val initialStock: Int,
    @SerializedName("shelfLocation") val shelfLocation: String,
    @SerializedName("price") val price: Double
)

data class UpdateStockRequest(
    @SerializedName("adjustment") val adjustment: Int
)