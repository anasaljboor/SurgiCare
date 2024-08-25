package com.example.surgicare.SignIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import com.example.surgicare.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthClient(private val context: Context, private val oneTapClient: SignInClient) {
    private val auth = Firebase.auth
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun getSigninWithintent(intent: Intent): signinresults {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {

            val user = auth.signInWithCredential(googleCredentials).await().user
            if (user != null) {
                println(user.displayName)
            }
            user?.let {
                // Save user role as "patient" in Firestore
                val userData = UserData(
                    userId = it.uid,
                    username = it.displayName,
                    profilePictureUrl = it.photoUrl?.toString().toString(),
                    role = "patient"
                )
                firestore.collection("Users").document(it.uid).set(userData).await()
            }
            signinresults(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString().toString(),
                        role = "patient"
                    )
                }, errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            signinresults(data = null, errorMessage = e.message)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): signinresults {
        return try {
            val user = auth.signInWithEmailAndPassword(email, password).await().user
            if (user != null) {
                println(user.uid)
            }
            user?.let {
                // Save user role if not already in Firestore
                val userDoc = firestore.collection("Users").document(it.uid).get().await()
                if (!userDoc.exists()) {
                    val userData = UserData(
                        userId = it.uid,
                        username = it.displayName,
                        profilePictureUrl = it.photoUrl?.toString().toString(),
                        role = "patient"
                    )
                    firestore.collection("Users").document(it.uid).set(userData).await()
                }
            }
            signinresults(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString().toString(),
                        role = "patient"
                    )
                }, errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            signinresults(data = null, errorMessage = e.message)
        }
    }

    suspend fun registerPatientWithEmail(firstName: String, lastName: String, email: String, password: String): signinresults {
        return try {
            val user = auth.createUserWithEmailAndPassword(email, password).await().user
            user?.let {
                val userData = UserData(
                    userId = it.uid,
                    username = "$firstName $lastName",
                    profilePictureUrl = it.photoUrl?.toString().toString(),
                    role = "patient"
                )
                firestore.collection("Users").document(it.uid).set(userData).await()
            }
            signinresults(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = "$firstName $lastName",
                        profilePictureUrl = photoUrl?.toString().toString(),
                        role = "patient"
                    )
                }, errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            signinresults(data = null, errorMessage = e.message)
        }
    }



    suspend fun signOut() {
        try {
            oneTapClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): UserData? = auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString().toString(),
        )
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder().setGoogleIdTokenRequestOptions(
            GoogleIdTokenRequestOptions.builder().setSupported(true)
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id)).build()
        ).setAutoSelectEnabled(true).build()
    }
}
