package com.example.surgicare.Notifications

import com.example.surgicare.notifications.NotificationData
import com.example.surgicare.notifications.NotificationViewModel
import com.example.surgicare.notifications.NotificationViewModelFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.surgicare.SignIn.GoogleAuthClient
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationScreen(userId: String, googleAuthClient: GoogleAuthClient) {
    // Initialize the NotificationViewModel with the GoogleAuthClient using a factory
    val viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModelFactory(googleAuthClient)
    )

    var notifications by remember { mutableStateOf(listOf<NotificationData>()) }

    // Fetch notifications from Firestore
    LaunchedEffect(userId) {
        viewModel.fetchNotifications {
            notifications = it
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Notifications", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(8.dp))

        if (notifications.isEmpty()) {
            Text(text = "No notifications", modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(notifications) { notification ->
                    NotificationItem(notification = notification)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: NotificationData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Format and display the timestamp
            notification.timestamp?.let { date ->
                val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                Text(
                    text = dateFormat.format(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatTimestamp(date: Date?): String {
    return if (date != null) {
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        sdf.format(date)
    } else {
        "Unknown time"
    }
}
