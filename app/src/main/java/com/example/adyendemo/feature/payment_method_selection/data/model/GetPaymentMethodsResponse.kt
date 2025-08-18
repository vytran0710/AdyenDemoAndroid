package com.example.adyendemo.feature.payment_method_selection.data.model


import com.google.gson.annotations.SerializedName

data class GetPaymentMethodsResponse(
    @SerializedName("paymentMethods")
    val paymentMethods: List<PaymentMethod>
)