package com.example.librarymobile.data.model

data class InventoryStatusDTO(
    val totalTitles: Int,
    val totalCopies: Int,
    val availableCount: Int,
    val borrowedCount: Int,
    val damagedCount: Int,
    val lostCount: Int
)
