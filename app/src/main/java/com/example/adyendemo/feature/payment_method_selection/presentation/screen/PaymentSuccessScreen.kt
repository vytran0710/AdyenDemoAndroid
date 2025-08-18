package com.example.adyendemo.feature.payment_method_selection.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.adyendemo.R

@Composable
fun PaymentSuccessScreen(
    modifier: Modifier = Modifier,
    onPaymentMethodSelectionRequested: () -> Unit
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                text = stringResource(id = R.string.payment_was_successful_)
            )
            Button(
                modifier = Modifier
                    .padding(all = 16.dp),
                onClick = onPaymentMethodSelectionRequested,
                content = {
                    Text(text = stringResource(id = R.string.ok))
                }
            )
        }
    }
}