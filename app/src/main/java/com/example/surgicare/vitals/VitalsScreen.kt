package com.example.surgicare.vitals

import VitalsViewModelFactory
import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import coil.compose.rememberAsyncImagePainter
import com.example.surgicare.DataVisualization.DataVisualizationScreen
import com.example.surgicare.SignIn.GoogleAuthClient

sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Vitals : Screen("VitalsScreen", Icons.Default.Home, "Vitals")
    object DataVisualization : Screen("DataVisualizationScreen", Icons.Default.BarChart, "Data")
    object Notifications : Screen("NotificationsScreen", Icons.Default.Notifications, "Notifications")
}



// Bottom navigation bar
@Composable
fun BottomNavigationBar(navController: NavHostController, items: List<Screen>) {
    NavigationBar {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}




// VitalsScreen as the home screen
@Composable
fun VitalsScreen(googleAuthClient: GoogleAuthClient) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var selectedVital by remember { mutableStateOf("") }

    // Use the ViewModel
    val viewModel: VitalsViewModel = viewModel(
        factory = VitalsViewModelFactory(
            application = LocalContext.current.applicationContext as Application,
            googleAuthClient = googleAuthClient
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // User Profile Image at the Top
        UserProfileHeader(googleAuthClient = googleAuthClient)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Vitals Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Heart Rate Card
                    VitalCard(
                        title = "Heart Rate",
                        value = viewModel.heartRate,
                        backgroundColor = Color(0xFFE0F7FA),
                        onClick = {
                            selectedVital = "Heart Rate"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Blood Pressure Card
                    VitalCard(
                        title = "Blood Pressure",
                        value = viewModel.bloodPressure,
                        backgroundColor = Color(0xFFFFEBEE),
                        onClick = {
                            selectedVital = "Blood Pressure"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Temperature Card
                    VitalCard(
                        title = "Temperature",
                        value = viewModel.temperature,
                        backgroundColor = Color(0xFFE0F7FA),
                        onClick = {
                            selectedVital = "Temperature"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Blood Group Card
                    VitalCard(
                        title = "Blood Group",
                        value = viewModel.bloodGroup,
                        backgroundColor = Color(0xFFE0F7FA),
                        onClick = {
                            selectedVital = "Blood Group"
                            isDialogOpen = true
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // SpO2 Card
                    VitalCard(
                        title = "SpO2",
                        value = viewModel.spO2,
                        backgroundColor = Color(0xFFE8F5E9),
                        onClick = {
                            selectedVital = "SpO2"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Respiratory Rate Card
                    VitalCard(
                        title = "Respiratory Rate",
                        value = viewModel.respiratoryRate,
                        backgroundColor = Color(0xFFFFEBEE),
                        onClick = {
                            selectedVital = "Respiratory Rate"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Weight Card
                    VitalCard(
                        title = "Weight",
                        value = viewModel.weight,
                        backgroundColor = Color(0xFFE8F5E9),
                        onClick = {
                            selectedVital = "Weight"
                            isDialogOpen = true
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Timestamp Card
                    VitalCard(
                        title = "Last Updated",
                        value = viewModel.timestamp,
                        backgroundColor = Color(0xFFE8F5E9),
                        onClick = { /* Do nothing */ }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Report Section
            Text(
                text = "Report",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ReportCard(
                title = "Your Health Report",
                files = "Summary of your vitals",
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Input Dialog for updating vitals
        if (isDialogOpen) {
            InputDialog(
                title = "Update $selectedVital",
                onDismiss = { isDialogOpen = false },
                onConfirm = { newValue ->
                    // Handle updating the specific vital
                    when (selectedVital) {
                        "Heart Rate" -> {
                            viewModel.heartRate = newValue.toIntOrNull()?.toString() ?: "Invalid"
                        }
                        "Blood Pressure" -> {
                            viewModel.bloodPressure = newValue
                        }
                        "SpO2" -> {
                            viewModel.spO2 = newValue.toIntOrNull()?.toString() ?: "Invalid"
                        }
                        "Temperature" -> {
                            viewModel.temperature = newValue.toFloatOrNull()?.toString() ?: "Invalid"
                        }
                        "Respiratory Rate" -> {
                            viewModel.respiratoryRate = newValue.toIntOrNull()?.toString() ?: "Invalid"
                        }
                        "Weight" -> {
                            viewModel.weight = newValue.toFloatOrNull()?.toString() ?: "Invalid"
                        }
                        "Blood Group" -> {
                            viewModel.bloodGroup = newValue
                        }
                    }

                    // Update vitals in Firestore
                    viewModel.addVitals(
                        heartRate = viewModel.heartRate.toIntOrNull() ?: 0,
                        bloodPressure = viewModel.bloodPressure,
                        weight = viewModel.weight.toFloatOrNull() ?: 0f,
                        bloodGroup = viewModel.bloodGroup,
                        spO2 = viewModel.spO2.toIntOrNull() ?: 0,
                        temperature = viewModel.temperature.toFloatOrNull() ?: 0f,
                        respiratoryRate = viewModel.respiratoryRate.toIntOrNull() ?: 0
                    )

                    isDialogOpen = false
                }
            )
        }
    }
}

// User profile header with image and name
@Composable
fun UserProfileHeader(googleAuthClient: GoogleAuthClient) {
    val user = googleAuthClient.getSignedInUser()
    val imageUrl = user?.profilePictureUrl

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 16.dp, vertical = 8.dp) // Adjusted padding
    ) {
        // Welcome statement
        Text(
            text = "Welcome,",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Profile Image (only if imageUrl is not null)
            if (imageUrl != null) {
                val painter = rememberAsyncImagePainter(
                    model = imageUrl
                )

                Image(
                    painter = painter,
                    contentDescription = "User Profile Image",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))
            }

            // User Name
            Text(
                text = user?.username ?: "User Name",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}


// Placeholder for Data Visualization Screen


// Placeholder for Notifications Screen
@Composable
fun NotificationsScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Notifications Screen")
    }
}

// VitalCard composable for displaying vitals
@Composable
fun VitalCard(
    title: String,
    value: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (value == "Loading...") {
                CircularProgressIndicator()
            } else {
                Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Light)
        }
    }
}

// ReportCard composable
@Composable
fun ReportCard(title: String, files: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = files, fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}

// InputDialog for updating vitals
@Composable
fun InputDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("Enter value") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(inputText) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
