package com.example.librarymobile.data.model

data class NotificationDTO(
    val notificationId: Long,
    val title: String,
    val content: String,
    val createdAt: String,
    val isRead: Boolean
)
