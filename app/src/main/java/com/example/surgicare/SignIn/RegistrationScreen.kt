package com.example.surgicare.SignIn

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.surgicare.R
import com.example.surgicare.ui.theme.DarkTealGreen
import com.example.surgicare.ui.theme.PrimaryTealGreen
import kotlinx.coroutines.launch

@Composable
fun RegistrationScreen(
    state: RegistrationState,
    onRegisterClick: (String, String, String, String) -> Unit,
    googleAuthClient: GoogleAuthClient,
    onSignInClick: () -> Unit
) {
    val context = LocalContext.current

    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(state.registrationError) {
        state.registrationError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }


    Surface {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Hey there,",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Text(
                text = "Create an Account",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))

            // First Name Field
            LoginTextfield(
                label = "First Name",
                trailing = "",
                modifier = Modifier.fillMaxWidth(),
                email = firstName
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Last Name Field
            LoginTextfield(
                label = "Last Name",
                trailing = "",
                modifier = Modifier.fillMaxWidth(),
                email = lastName
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            LoginTextfield(
                label = "Email",
                trailing = "",
                modifier = Modifier.fillMaxWidth(),
                email = email
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            LoginTextfield(
                label = "Password",
                trailing = "",
                modifier = Modifier.fillMaxWidth(),
                email = password
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Register Button
            Button(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    onRegisterClick(firstName.value, lastName.value, email.value, password.value)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) DarkTealGreen else PrimaryTealGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Google Sign-In Button
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Or",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
                )
                Spacer(modifier = Modifier.height(20.dp))
                GoogleLoginButton(
                    iconResId = R.drawable.google,
                    onClick = onSignInClick,
                    text = "Register with Google",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))

            // Already have an account? Login
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PrimaryTealGreen,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.clickable { onSignInClick() } // Make text clickable to navigate to Sign-In
                )
            }
        }

        // Display a loading indicator while signing in
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
