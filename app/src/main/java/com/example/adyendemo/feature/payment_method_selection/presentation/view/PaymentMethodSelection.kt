package com.example.adyendemo.feature.payment_method_selection.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod

@Composable
fun PaymentMethodSelection(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme(),
    paymentMethod: PaymentMethod,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val contentColor = if (!darkTheme) {
        Color.Black
    } else {
        Color.White
    }
    Row(
        modifier = modifier
            .clickable {
                if (!isSelected) {
                    onSelected.invoke()
                }
            }
            .border(width = 1.dp, color = contentColor)
            .padding(all = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = paymentMethod.name,
            color = contentColor
        )
        Spacer(
            modifier = Modifier
                .weight(weight = 1F)
        )
        if (isSelected) {
            Icon(
                modifier = Modifier
                    .size(size = 24.dp),
                imageVector = Icons.Filled.Done,
                tint = contentColor,
                contentDescription = "${paymentMethod.name} is selected"
            )
        } else {
            Box(
                modifier = Modifier
                    .height(height = 24.dp)
            )
        }
    }
}