package com.example.surgicare.SignIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrationState(
    val isLoading: Boolean = false,
    val registrationError: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegistrationViewModel(private val googleAuthClient: GoogleAuthClient) : ViewModel() {
    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state

    fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val result = googleAuthClient.registerPatientWithEmail(firstName, lastName, email, password)
                if (result.errorMessage == null) {
                    _state.update { it.copy(isRegistrationSuccessful = true, isLoading = false) }
                } else {
                    _state.update { it.copy(registrationError = result.errorMessage, isLoading = false) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(registrationError = e.message, isLoading = false) }
            }
        }
    }

    fun resetState() {
        _state.update { RegistrationState() }
    }
}