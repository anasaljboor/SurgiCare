package com.example.surgicare.SignIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RegistrationViewModelFactory(
    private val googleAuthClient: GoogleAuthClient
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistrationViewModel::class.java)) {
            return RegistrationViewModel(googleAuthClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
