package com.example.adyendemo.feature.payment_method_selection.domain.repository

import com.example.adyendemo.feature.payment_method_selection.data.data_source.PaymentLocalDataSource
import com.example.adyendemo.feature.payment_method_selection.data.data_source.PaymentRemoteDataSource
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentRequest

class PaymentRepository(
    private val paymentRemoteDataSource: PaymentRemoteDataSource,
    private val paymentLocalDataSource: PaymentLocalDataSource
) {
    suspend fun getPaymentMethods(
        amount: Long,
        currency: String,
        countryCode: String,
        shopperLocale: String
    ) = paymentRemoteDataSource.getPaymentMethods(
        amount = amount,
        currency = currency,
        countryCode = countryCode,
        shopperLocale = shopperLocale
    )

    fun getPaymentLocales() = paymentLocalDataSource.getPaymentLocales()

    suspend fun getClientKey() = paymentRemoteDataSource.getClientKey()
    suspend fun submitPayment(request: PaymentRequest) =
        paymentRemoteDataSource.submitPayment(request = request)

    suspend fun processDetails(details: Map<String, Any>) =
        paymentRemoteDataSource.processDetails(details = details)
}