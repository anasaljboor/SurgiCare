package com.example.surgicare.SignIn

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.surgicare.ui.theme.focusedTextfieldText
import com.example.surgicare.ui.theme.textFieldContainer
import com.example.surgicare.ui.theme.unfocusedTextfieldText

@Composable
fun LoginTextfield(
    modifier: Modifier = Modifier,
    label: String,
    trailing: String
) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Color.Black

    TextField(
        modifier = modifier,
        value = "",
        onValueChange = {},
        label = {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = uiColor)
                },
        colors = TextFieldDefaults.colors(
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.unfocusedTextfieldText,
            focusedPlaceholderColor = MaterialTheme.colorScheme.focusedTextfieldText,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.textFieldContainer
        ),
        trailingIcon = {
            Text(text = trailing, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium), color = uiColor)
        }
    )
}