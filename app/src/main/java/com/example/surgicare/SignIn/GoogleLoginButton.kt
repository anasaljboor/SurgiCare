package com.example.surgicare.SignIn

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.surgicare.ui.theme.DarkTealGreen
import com.example.surgicare.ui.theme.PrimaryTealGreen

@Composable
fun GoogleLoginButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
    text: String
) {
    Row(
        modifier = modifier
            .googleLoginButton()  // Apply your custom modifier
            .clickable(onClick = onClick)
            .height(40.dp)
            .clip(RoundedCornerShape(4.dp)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isSystemInDarkTheme()) Color.White else Color.Black // Dynamically change color
            )
        )
    }
}

fun Modifier.googleLoginButton(): Modifier = composed {
    if (isSystemInDarkTheme()) {
        this.background(Color.Transparent).border(
            width = 1.dp,
            color = DarkTealGreen,
            shape = RoundedCornerShape(4.dp),
        )
    } else {
        background(PrimaryTealGreen)
    }
}
