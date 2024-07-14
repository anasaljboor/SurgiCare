package com.example.surgicare.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
val DarkTealGreen = Color(0xFF1E4C42)  // Darker version of teal for dark theme
val LightGray = Color(0xFFDCDCDC)
val PrimaryTealGreen = Color(0xFF3C887E)
val Cyan = Color(0xFF03DAC6)  // Cyan color for secondary color in dark theme
val LightPurple = Color(0xFFBB86FC)  // Light purple for tertiary color in dark theme
val LavenderBlush = Color(0xFFFFEBEE)
val DarkSurface = Color(0xFF1E1E1E)  // Dark surface color

val ColorScheme.focusedTextfieldText: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color.White else Color.Black

val ColorScheme.unfocusedTextfieldText: Color
    @Composable
    get() = if (isSystemInDarkTheme()) LightGray else DarkTealGreen

val ColorScheme.textFieldContainer: Color
    @Composable
    get() = if (isSystemInDarkTheme()) DarkSurface else LightGray
