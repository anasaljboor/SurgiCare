package com.example.surgicare.SignIn

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(private val googleAuthClient: GoogleAuthClient) : ViewModel() {
    private val _signInState = MutableStateFlow(SignInState())
    val state = _signInState.asStateFlow()

    fun signInWithGoogle(launchIntentSender: (IntentSender) -> Unit) {
        viewModelScope.launch {
            _signInState.update { it.copy(isLoading = true) }
            try {
                val intentSender = googleAuthClient.signIn()
                if (intentSender != null) {
                    // Launch the intent sender (triggered from the UI)
                    launchIntentSender(intentSender)
                } else {
                    _signInState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _signInState.update { it.copy(signInError = e.message, isLoading = false) }
            }
        }
    }

    fun handleSignInResult(intent: Intent) {
        viewModelScope.launch {
            try {
                val signInResult = googleAuthClient.getSigninWithintent(intent)
                onSignInResult(signInResult)
            } catch (e: Exception) {
                _signInState.update { it.copy(signInError = e.message) }
            }
        }
    }

    fun onSignInResult(result: signinresults) {
        _signInState.update {
            it.copy(
                isSignInSuccessful = result.data != null, // Ensure this is set correctly
                signInError = result.errorMessage,
                isLoading = false
            )
        }
    }
}