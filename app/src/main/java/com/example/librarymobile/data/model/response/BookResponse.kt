package com.example.librarymobile.data.model.response

import com.google.gson.annotations.SerializedName

data class BookResponse(
    @SerializedName("bookId") val bookId: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("author") val author: String?,
    @SerializedName("isbn") val isbn: String?,
    @SerializedName("genre") val category: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("totalStock") val totalStock: Int?,
    @SerializedName("shelfLocation") val shelfLocation: String?,
    @SerializedName("price") val price: Double?
)

data class BookCopyResponse(
    @SerializedName("copyId") val copyId: Long?,
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("bookId") val bookId: Long?
)