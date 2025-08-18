package com.example.adyendemo.feature.payment_method_selection.presentation.view_model

import com.adyen.checkout.components.core.action.Action

data class PaymentDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val clientKey: String? = null,
    val action: Action? = null,
    val reference: String? = null,
    val isAuthorized: Boolean = false
)