package com.example.surgicare.SignIn


data class UserData(
    val userId: String?,
    val username: String?,
    val profilePictureUrl: String,
    val role: String? = null,
    val email: String

)
