package com.example.surgicare.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.surgicare.SignIn.GoogleAuthClient

class NotificationViewModelFactory(
    private val googleAuthClient: GoogleAuthClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotificationViewModel::class.java)) {
            return NotificationViewModel(googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
