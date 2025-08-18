package com.example.adyendemo.module.network

import com.example.adyendemo.feature.payment_method_selection.data.model.GetPaymentMethodsResponse
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@JvmSuppressWildcards
interface NetworkService {
    @GET("/api/payments/getPaymentMethods")
    suspend fun getPaymentMethods(
        @Query("amount") amount: Long,
        @Query("currency") currency: String,
        @Query("countryCode") countryCode: String,
        @Query("shopperLocale") shopperLocale: String
    ): Response<GetPaymentMethodsResponse>

    @GET("api/payments/getClientKey")
    suspend fun getClientKey(): Response<String>

    @POST("/api/payments/initiate")
    suspend fun initiate(@Body request: PaymentRequest): Response<Map<String, Any>>

    @POST("/api/payments/processDetails")
    suspend fun processDetails(@Body details: Map<String, Any>): Response<Map<String, Any>>
}