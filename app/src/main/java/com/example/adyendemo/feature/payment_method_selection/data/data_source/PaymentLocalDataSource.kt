package com.example.adyendemo.feature.payment_method_selection.data.data_source

import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale

class PaymentLocalDataSource {
    fun getPaymentLocales(): List<PaymentLocale> {
        return listOf(
            PaymentLocale(id = 1, currency = "USD", countryCode = "US", shopperLocale = "en-US"),
            PaymentLocale(id = 2, currency = "EUR", countryCode = "DE", shopperLocale = "de-DE"),
            PaymentLocale(id = 3, currency = "GBP", countryCode = "GB", shopperLocale = "en-GB")
        )
    }
}