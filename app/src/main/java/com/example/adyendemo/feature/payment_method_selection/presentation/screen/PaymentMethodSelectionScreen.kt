package com.example.adyendemo.feature.payment_method_selection.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.adyendemo.R
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod
import com.example.adyendemo.feature.payment_method_selection.presentation.view.PaymentLocaleSelection
import com.example.adyendemo.feature.payment_method_selection.presentation.view.PaymentMethodSelection
import com.example.adyendemo.feature.payment_method_selection.presentation.view_model.PaymentMethodSelectionViewModel
import com.example.adyendemo.feature.payment_method_selection.presentation.view_model.PaymentMethodSelectionViewModel.Companion.INITIAL_AMOUNT
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PaymentMethodSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: PaymentMethodSelectionViewModel = koinViewModel(),
    onPaymentDetailRequested: (paymentLocale: PaymentLocale, paymentMethod: PaymentMethod, amount: Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isLoading = state.isLoading
    val errorMessage = state.errorMessage
    val amount = state.amount
    val selectedPaymentLocale = state.selectedPaymentLocale
    val selectedPaymentMethod = state.selectedPaymentMethod
    val paymentLocales = state.paymentLocales ?: listOf()
    val data = state.data
    val paymentMethods = data?.paymentMethods ?: listOf()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            Button(
                modifier = Modifier
                    .padding(all = 16.dp)
                    .fillMaxWidth(),
                onClick = {
                    val selectedPaymentLocale = selectedPaymentLocale ?: return@Button
                    val selectedPaymentMethod = selectedPaymentMethod ?: return@Button
                    onPaymentDetailRequested.invoke(
                        selectedPaymentLocale,
                        selectedPaymentMethod,
                        amount
                    )
                },
                content = {
                    Text(text = stringResource(id = R.string.checkout))
                },
                enabled = selectedPaymentMethod != null && amount >= INITIAL_AMOUNT
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues = innerPadding)
                .fillMaxSize()
        ) {
            item {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.locales)
                )
            }
            items(items = paymentLocales) { item ->
                PaymentLocaleSelection(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    paymentLocale = item,
                    isSelected = item.id == selectedPaymentLocale?.id,
                    onSelected = {
                        viewModel.setSelectedPaymentLocale(paymentLocale = item)
                    }
                )
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.amount)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        value = amount.toString(),
                        onValueChange = { value ->
                            value.toLongOrNull()?.let {
                                viewModel.setAmount(amount = it)
                            }
                        },
                        label = {
                            Text(text = stringResource(id = R.string.amount))
                        },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                    selectedPaymentLocale?.currency?.let {
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = stringResource(
                                id = R.string.correct_amount_,
                                formatArgs = arrayOf((amount.toFloat() / 100F).toString(), it)
                            )
                        )
                        Text(
                            modifier = Modifier
                                .padding(top = 8.dp),
                            text = stringResource(
                                id = R.string.minimum_amount_,
                                formatArgs = arrayOf(
                                    (INITIAL_AMOUNT.toFloat() / 100F).toString(),
                                    it
                                )
                            )
                        )
                    }
                }
            }
            item {
                Text(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    text = stringResource(id = R.string.payment_methods)
                )
            }
            item {
                when {
                    isLoading -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                            )
                        }
                    }

                    errorMessage != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                                    .fillMaxWidth(),
                                text = errorMessage,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                                    .fillMaxWidth(),
                                onClick = {
                                    viewModel.retryGetPaymentMethods()
                                },
                                content = {
                                    Text(text = stringResource(id = R.string.retry))
                                }
                            )
                        }
                    }
                }
            }
            itemsIndexed(
                items = paymentMethods,
                key = { index, item -> item.type }) { index, item ->
                val paddingValues = when (index) {
                    paymentMethods.size - 1 -> {
                        PaddingValues(all = 16.dp)
                    }

                    else -> {
                        PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp)
                    }
                }
                PaymentMethodSelection(
                    modifier = Modifier
                        .padding(paddingValues = paddingValues)
                        .fillMaxWidth(),
                    paymentMethod = item,
                    isSelected = item.type == selectedPaymentMethod?.type,
                    onSelected = {
                        viewModel.setSelectedPaymentMethod(paymentMethod = item)
                    }
                )
            }
        }
    }
}