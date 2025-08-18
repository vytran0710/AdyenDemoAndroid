package com.example.adyendemo.feature.payment_method_selection.presentation.view_model

import com.example.adyendemo.feature.payment_method_selection.data.model.GetPaymentMethodsResponse
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod

data class PaymentMethodSelectionState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val paymentLocales: List<PaymentLocale>? = null,
    val data: GetPaymentMethodsResponse? = null,
    val amount: Long,
    val selectedPaymentLocale: PaymentLocale? = null,
    val selectedPaymentMethod: PaymentMethod? = null
)