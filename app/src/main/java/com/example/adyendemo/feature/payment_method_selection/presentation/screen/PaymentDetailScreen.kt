package com.example.adyendemo.feature.payment_method_selection.presentation.screen

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.adyen.checkout.components.core.PaymentMethodTypes
import com.example.adyendemo.R
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod
import com.example.adyendemo.feature.payment_method_selection.presentation.view.CreditCardPayment
import com.example.adyendemo.feature.payment_method_selection.presentation.view_model.PaymentDetailViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentDetailScreen(
    modifier: Modifier = Modifier,
    paymentLocale: PaymentLocale,
    paymentMethod: PaymentMethod,
    amount: Long,
    intent: Intent? = null,
    onIntentHandled: () -> Unit,
    viewModel: PaymentDetailViewModel = koinViewModel(),
    onPaymentSuccessRequested: () -> Unit
) {
    Scaffold(
        modifier = modifier
    ) { innerPadding ->
        val state by viewModel.state.collectAsState()
        val isLoading = state.isLoading
        val errorMessage = state.errorMessage
        val reference = state.reference
        val isAuthorized = state.isAuthorized
        LaunchedEffect(key1 = isAuthorized) {
            if (isAuthorized) {
                onPaymentSuccessRequested.invoke()
            }
        }
        Column(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
                return@Column
            }
            if (errorMessage != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
                        text = errorMessage
                    )
                    Button(
                        modifier = Modifier
                            .padding(all = 16.dp),
                        onClick = {
                            viewModel.retryFetchPaymentInfo()
                        },
                        content = {
                            Text(text = stringResource(id = R.string.retry))
                        }
                    )
                }
                return@Column
            }
            if (reference == null) return@Column
            when (paymentMethod.type) {
                PaymentMethodTypes.SCHEME -> {
                    CreditCardPayment(
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .fillMaxWidth(),
                        paymentMethod = paymentMethod.adyenPaymentMethod,
                        reference = reference,
                        clientKey = state.clientKey ?: return@Column,
                        onSubmit = { state ->
                            viewModel.submitPayment(
                                state = state,
                                paymentLocale = paymentLocale,
                                amount = amount,
                                reference = reference
                            )
                        },
                        onProcessDetails = {
                            viewModel.processDetails(actionComponentData = it)
                        },
                        onError = {
                            viewModel.setError(error = it)
                        },
                        action = state.action,
                        onActionHandled = {
                            viewModel.onActionHandled()
                        },
                        intent = intent,
                        onIntentHandled = onIntentHandled
                    )
                }

                else -> {}
            }
        }
    }
}