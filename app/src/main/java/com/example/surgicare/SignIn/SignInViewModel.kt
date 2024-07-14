package com.example.surgicare.SignIn

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel : ViewModel() {
    private val _signInState = MutableStateFlow(SignInState())
    val state = _signInState.asStateFlow()

    fun onSignInResult(result: signinresults) {
        _signInState.update {it.copy(
            isSignInSuccessful = result.data != null,
            signInError = result.errorMessage
        )

        }

    }
    fun resetSignInState() {
        _signInState.update { SignInState() }
    }

}