// NotificationData.kt
package com.example.surgicare.notifications

import java.util.Date

data class NotificationData(
    val title: String = "",
    val message: String = "",
    val timestamp: Date? = null,
)
