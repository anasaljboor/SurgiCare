package com.example.surgicare.SignIn

data class SignInState(
    val isLoading: Boolean = false, // Indicates if the sign-in process is ongoing
    val isSignInSuccessful: Boolean = false, // Indicates if the sign-in was successful
    val signInError: String? = null // Holds any error messages during the sign-in process
)
