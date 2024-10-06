package com.example.surgicare

import DataVisualizationViewModelFactory
import SignInViewModelFactory
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.surgicare.DataVisualization.DataVisualizationViewModel
import com.example.surgicare.DataVisualization.DataVisualizationScreen
import com.example.surgicare.SignIn.GoogleAuthClient
import com.example.surgicare.SignIn.SignInScreen
import com.example.surgicare.SignIn.SignInViewModel
import com.example.surgicare.SignIn.RegistrationScreen
import com.example.surgicare.SignIn.RegistrationViewModel
import com.example.surgicare.SignIn.RegistrationViewModelFactory
import com.example.surgicare.notifications.NotificationHelper
import com.example.surgicare.ui.theme.SurgiCareTheme
import com.example.surgicare.vitals.VitalsScreen
import com.example.surgicare.vitals.BottomNavigationBar
import com.example.surgicare.vitals.NotificationsScreen
import com.example.surgicare.vitals.Screen
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_POST_NOTIFICATION = 1001

    // Lazy initialization of GoogleAuthClient
    private val googleAuthClient by lazy {
        GoogleAuthClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    private lateinit var viewModel: SignInViewModel
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()

        // Initialize NotificationHelper
        notificationHelper = NotificationHelper(applicationContext, googleAuthClient)

        // Initialize the ViewModel
        val viewModelFactory = SignInViewModelFactory(googleAuthClient)
        viewModel = ViewModelProvider(this, viewModelFactory)[SignInViewModel::class.java]

        // Assuming userId is retrieved from the sign-in process or another user session
        val userId = "some_user_id" // Replace with actual user ID retrieval logic

        // Initialize DataVisualizationViewModel with the correct factory



        setContent {
            SurgiCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Define the list of screens for the bottom navigation bar
                    val items = listOf(Screen.Vitals, Screen.DataVisualization, Screen.Notifications)

                    // Layout containing both the NavHost and BottomNavigationBar
                    Scaffold(
                        bottomBar = {
                            // Only show the BottomNavigationBar if we are not on the SignIn or Registration screen
                            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                            if (currentRoute != "SignInScreen" && currentRoute != "RegistrationScreen") {
                                BottomNavigationBar(navController = navController, items = items)
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = "SignInScreen",
                            modifier = Modifier.padding(innerPadding) // Adjust content for the navigation bar
                        ) {
                            composable("SignInScreen") {
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                val launcher = rememberLauncherForActivityResult(
                                    contract = ActivityResultContracts.StartIntentSenderForResult(),
                                    onResult = { result ->
                                        if (result.resultCode == Activity.RESULT_OK) {
                                            lifecycleScope.launch {
                                                val signInResult = googleAuthClient.getSignInWithIntent(
                                                    intent = result.data ?: return@launch
                                                )
                                                viewModel.onSignInResult(signInResult)

                                                if (signInResult.data != null) {
                                                    notificationHelper.scheduleMedicationReminder(isMorning = true)
                                                    notificationHelper.scheduleMedicationReminder(isMorning = false)
                                                    notificationHelper.scheduleDailyHealthCheckIn()
                                                    navController.navigate("VitalsScreen")
                                                } else {
                                                    Toast.makeText(
                                                        this@MainActivity,
                                                        "Sign-in failed: ${signInResult.errorMessage}",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                )

                                SignInScreen(
                                    state = state,
                                    googleAuthClient = googleAuthClient,
                                    onSignInClick = {
                                        lifecycleScope.launch {
                                            val signInIntentSender = googleAuthClient.signIn()
                                            launcher.launch(
                                                IntentSenderRequest.Builder(
                                                    signInIntentSender ?: return@launch
                                                ).build()
                                            )
                                        }
                                    },
                                    onNavigateToRegister = {
                                        navController.navigate("RegistrationScreen")
                                    },
                                    onNavigateToVitals = {
                                        navController.navigate("VitalsScreen")
                                    }
                                )
                            }
                            composable("VitalsScreen") {
                                VitalsScreen(googleAuthClient = googleAuthClient)
                            }
                            composable("DataVisualizationScreen") {
                                val dataVisualizationViewModel: DataVisualizationViewModel = viewModel(
                                    factory = DataVisualizationViewModelFactory(googleAuthClient, userId)
                                )
                                DataVisualizationScreen(
                                    viewModel = dataVisualizationViewModel,
                                    userId = userId
                                )
                            }

                            composable("NotificationsScreen") {
                                NotificationsScreen()
                            }
                            composable("RegistrationScreen") {
                                val registrationViewModel: RegistrationViewModel = viewModel(
                                    factory = RegistrationViewModelFactory(googleAuthClient)
                                )
                                val registrationState by registrationViewModel.state.collectAsStateWithLifecycle()

                                RegistrationScreen(
                                    state = registrationState,
                                    onRegisterClick = { firstName, lastName, email, password ->
                                        registrationViewModel.registerUser(firstName, lastName, email, password)
                                        navController.navigate("SignInScreen")
                                    },
                                    googleAuthClient = googleAuthClient,
                                    onSignInClick = {
                                        navController.navigate("SignInScreen")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_POST_NOTIFICATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_POST_NOTIFICATION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    "Notification permission is required for notifications.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
