@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.surgicare.SignIn
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.focusRequester


@Composable
fun LoginTextfield(
    label: String,
    trailing: String = "",
    modifier: Modifier = Modifier,
    onTrailingClick: (() -> Unit)? = null
    ,email :MutableState<String>
) {


    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    TextField(
        value = email.value,
        onValueChange = { newText -> email.value = newText },
        label = { Text(text = label) },
        trailingIcon = {
            if (trailing.isNotEmpty() && onTrailingClick != null) {
                Text(
                    text = trailing,
                    modifier = Modifier.clickable { onTrailingClick() }
                )
            }
        },
        modifier = modifier
            .onFocusChanged { focusState -> isFocused = focusState.isFocused }
            .focusRequester(focusRequester),
        colors = TextFieldDefaults.textFieldColors(
            focusedIndicatorColor = if (isFocused) Color.Blue else Color.Gray
        )
    )
}
