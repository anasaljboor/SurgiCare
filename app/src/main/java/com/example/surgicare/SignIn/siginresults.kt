package com.example.surgicare.SignIn



data class signinresults(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String?,
    val username: String?,
    val profilePictureUrl: String,
    val role: String? = null

)