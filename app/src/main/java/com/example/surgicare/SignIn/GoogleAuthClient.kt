package com.example.surgicare.SignIn

import PatientData
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.surgicare.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {

    private val auth = Firebase.auth
    internal val firestore = FirebaseFirestore.getInstance()

    // Sign in using Google OneTap
    suspend fun signIn(): IntentSender? {
        return try {
            val result = oneTapClient.beginSignIn(buildSignInRequest()).await()
            result?.pendingIntent?.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    // Process the sign-in result and store user data in Firestore
    suspend fun getSignInWithIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)

        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user

            // If the user is authenticated
            user?.let {
                val userId = it.uid

                // Save user data in Firestore if not already present
                if (!isUserInFirestore(userId)) {
                    saveUserToFirestore(
                        userId = userId,
                        username = it.displayName ?: "Unknown User",
                        email = it.email ?: "",
                        profilePictureUrl = it.photoUrl?.toString() ?: "",
                        role = "patient"
                    )
                } else {
                    println("User already exists in Firestore: $userId")
                }
            }

            // Return successful result
            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName ?: "Unknown User",
                        email = it.email ?: "",
                        profilePictureUrl = it.photoUrl?.toString() ?: "",
                        role = "patient"
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // Sign in with email and password
    suspend fun signInWithEmail(email: String, password: String): SignInResult {
        return try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user

            user?.let {
                // Check if user already exists in Firestore
                if (!isUserInFirestore(it.uid)) {
                    saveUserToFirestore(
                        userId = it.uid,
                        username = it.displayName ?: "Unknown User",
                        email = it.email ?: "",
                        profilePictureUrl = it.photoUrl?.toString() ?: "",
                        role = "patient"
                    )
                } else {
                    println("User already exists in Firestore: ${it.uid}")
                }
            }

            // Return successful result
            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName ?: "Unknown User",
                        email = it.email ?: "",
                        profilePictureUrl = it.photoUrl?.toString() ?: "",
                        role = "patient"
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // Register a new patient with email and password
    suspend fun registerPatientWithEmail(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): SignInResult {
        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            user?.let {
                saveUserToFirestore(
                    userId = it.uid,
                    username = "$firstName $lastName",
                    email = email,
                    profilePictureUrl = it.photoUrl?.toString() ?: "",
                    role = "patient"
                )
                println("New patient registered: ${it.uid}")
            }

            // Return successful result
            SignInResult(
                data = user?.let {
                    UserData(
                        userId = it.uid,
                        username = "$firstName $lastName",
                        email = email,
                        profilePictureUrl = it.photoUrl?.toString() ?: "",
                        role = "patient"
                    )
                },
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }

    // Sign out the user
    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    // Get currently signed-in user
    fun getSignedInUser(): UserData? = auth.currentUser?.let {
        UserData(
            userId = it.uid,
            username = it.displayName ?: "Unknown User",
            email = it.email ?: "",
            profilePictureUrl = it.photoUrl?.toString() ?: "",
            role = "patient"
        )
    }

    // Build the sign-in request for Google OneTap
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .build()
        ).setAutoSelectEnabled(true).build()
    }

    // Helper function to check if the user is already in Firestore
    private suspend fun isUserInFirestore(userId: String): Boolean {
        val userDoc = firestore.collection("Users").document(userId).get().await()
        return userDoc.exists()
    }

    // Helper function to save user data to Firestore
    private suspend fun saveUserToFirestore(
        userId: String,
        username: String,
        email: String,
        profilePictureUrl: String,
        role: String
    ) {
        val userData = UserData(
            userId = userId,
            username = username,
            email = email,
            profilePictureUrl = profilePictureUrl,
            role = role
        )
        firestore.collection("Users").document(userId).set(userData).await()

        // Also add to the patients collection if needed
        val patientData = PatientData(
            userId = userId,
            firstName = username.split(" ").firstOrNull() ?: "Unknown",
            lastName = username.split(" ").lastOrNull() ?: "Unknown",

        )
        firestore.collection("patients").document(userId).set(patientData).await()
    }
}
