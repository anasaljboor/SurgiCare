package com.example.surgicare.SignIn

    import android.widget.Toast
    import androidx.compose.foundation.Image
    import androidx.compose.foundation.isSystemInDarkTheme
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.ColumnScope
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxHeight
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.material3.Button
    import androidx.compose.material3.ButtonDefaults
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Surface
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
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

    @Composable

    fun SignInScreen(state: SignInState, onSignInClick: () -> Unit) {
        val context = LocalContext.current
        LaunchedEffect(key1 = state.signInError) {
            state.signInError?.let { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
        Surface{
            Column(modifier = Modifier.fillMaxSize()) {
                TopSection()
                Spacer(modifier = Modifier.height(36.dp))
                googlebuttonSection(onSignInClick)
                RegestraionSection()
            }
        }

    }

@Composable
private fun RegestraionSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Box(
        modifier = Modifier
            .fillMaxHeight(fraction = 0.8f)
            .fillMaxWidth(), contentAlignment = Alignment.BottomCenter
    )
    {
        Text(text = buildAnnotatedString {
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
        }
        )
    }
}

@Composable
    private fun googlebuttonSection(onSignInClick: () -> Unit) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp)
        ) {
            LoginSection(onSignInClick)
            Spacer(modifier = Modifier.height(30.dp))
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "OR",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0xFF64748B))
                )
                Spacer(modifier = Modifier.height(20.dp))
                GoogleLoginButton(
                    iconResId = R.drawable.google, onClick = onSignInClick, text = "Sign in with Google",
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }

    @Composable
    private fun LoginSection(onSignInClick: () -> Unit) {
        LoginTextfield(label = "Email", trailing = "", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        LoginTextfield(label = "Password", trailing = "Forgot?", modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            onClick = { /*TODO*/ },
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
        run {
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
    }