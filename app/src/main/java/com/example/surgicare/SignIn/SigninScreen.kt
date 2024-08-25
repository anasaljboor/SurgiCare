package com.example.surgicare.SignIn

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.surgicare.R
import com.example.surgicare.ui.theme.DarkTealGreen
import com.example.surgicare.ui.theme.PrimaryTealGreen
import com.example.surgicare.ui.theme.Roboto
import kotlinx.coroutines.launch


@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit,
    googleAuthClient: GoogleAuthClient,
    onNavigateToRegister: () -> Unit,
    onNavigateToVitals: () -> Unit // Add this parameter for navigation to Vitals
) {
    val context = LocalContext.current

    // Handle sign-in errors
    LaunchedEffect(state.signInError) {
        state.signInError?.let { error ->
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Navigate to Vitals screen on successful sign-in
    LaunchedEffect(state.isSignInSuccessful) {
        if (state.isSignInSuccessful) {
            onNavigateToVitals()
        }
    }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            TopSection()
            Spacer(modifier = Modifier.height(36.dp))
            googleButtonSection(onSignInClick, googleAuthClient)
            Spacer(modifier = Modifier.height(36.dp))
            RegistrationSection(onNavigateToRegister)
        }

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



@Composable
private fun RegistrationSection(onNavigateToRegister: () -> Unit) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = Roboto
                    )
                ) {
                    append("Don't have an account? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = uiColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Roboto
                    )
                ) {
                    append("Create now")
                }
            },
            modifier = Modifier.clickable {
                onNavigateToRegister() // Trigger navigation when text is clicked
            }
        )
    }
}


@Composable
private fun googleButtonSection(onSignInClick: () -> Unit,googleAuthClient: GoogleAuthClient) {

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .padding(horizontal = 30.dp)

    ) {

        LoginSection(password = password , email = email, googleAuthClient = googleAuthClient)
        Spacer(modifier = Modifier.height(30.dp))
        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "OR",
                style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
            )
            Spacer(modifier = Modifier.height(20.dp))
            GoogleLoginButton(
                iconResId = R.drawable.google,
                onClick = onSignInClick,
                text = "Sign in with Google",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

@Composable
fun LoginSection(email :MutableState<String>, password :MutableState<String>,googleAuthClient: GoogleAuthClient) {
    val coroutineScope = rememberCoroutineScope()
    suspend fun Login(){
    googleAuthClient.signInWithEmail(email=email.value,password=password.value)
    }

    LoginTextfield(label = "Email", trailing = "", modifier = Modifier.fillMaxWidth(),email = email)
    Spacer(modifier = Modifier.height(16.dp))
    LoginTextfield(label = "Password", trailing = "Forgot?", modifier = Modifier.fillMaxWidth(),email = password)
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .fillMaxWidth()
            .height(40.dp),
        onClick = {
            coroutineScope.launch {
                Login()
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) DarkTealGreen else PrimaryTealGreen,
            contentColor = Color.White
        )
    ) {
        Text(
            text = "Log in",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}


@Composable
private fun ColumnScope.TopSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            painterResource(id = R.drawable.loginshapelight), contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = .46f),
            contentScale = ContentScale.FillBounds
        )
        Row(
            modifier = Modifier.padding(top = 80.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.surgicare),
                    style = MaterialTheme.typography.headlineMedium,
                    color = uiColor
                )
                Text(
                    text = stringResource(id = R.string.Caring_for_You_Virtually),
                    style = MaterialTheme.typography.titleMedium,
                    color = uiColor
                )
            }
        }
        Text(
            text = stringResource(id = R.string.Login_to_SurgiCare),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(Alignment.BottomCenter)
        )
    }

}
