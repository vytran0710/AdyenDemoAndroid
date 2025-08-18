package com.example.adyendemo.feature.payment_method_selection.data.model

data class PaymentLocale(
    val id: Int,
    val currency: String,
    val countryCode: String,
    val shopperLocale: String
) {
    val name: String
        get() {
            return "${this.countryCode} | ${this.shopperLocale} | ${this.currency}"
        }
}