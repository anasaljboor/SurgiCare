// NotificationViewModel.kt
package com.example.surgicare.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import com.example.surgicare.SignIn.GoogleAuthClient
import java.util.Date

class NotificationViewModel(private val googleAuthClient: GoogleAuthClient) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    fun fetchNotifications(onResult: (List<NotificationData>) -> Unit) {
        val userId = googleAuthClient.getSignedInUser()?.userId ?: return  // Retrieve the current user's ID

        viewModelScope.launch {
            firestore.collection("patients").document(userId)
                .collection("notifications")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { snapshot ->
                    val notifications = snapshot.documents.map { document ->
                        NotificationData(
                            title = document.getString("title") ?: "No title",
                            message = document.getString("message") ?: "No message",
                            timestamp = document.getTimestamp("timestamp")?.toDate() ?: Date()
                        )
                    }
                    onResult(notifications)
                }
                .addOnFailureListener { e ->
                    println("Error fetching notifications: ${e.message}")
                }
        }
    }
}
