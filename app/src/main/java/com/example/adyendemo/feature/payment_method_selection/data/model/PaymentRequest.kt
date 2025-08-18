package com.example.adyendemo.feature.payment_method_selection.data.model

data class PaymentRequest(
    val amount: Long,
    val currency: String,
    val reference: String,
    val redirectUrl: String,
    val channel: String,
    val paymentMethod: Map<String, Any>,
    val browserInfo: BrowserInfo
) {
    data class BrowserInfo(
        val userAgent: String,
        val acceptHeader: String
    )
}