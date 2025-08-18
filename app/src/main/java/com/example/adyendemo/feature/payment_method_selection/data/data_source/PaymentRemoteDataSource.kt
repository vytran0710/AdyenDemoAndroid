package com.example.adyendemo.feature.payment_method_selection.data.data_source

import com.example.adyendemo.feature.payment_method_selection.data.model.GetPaymentMethodsResponse
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentRequest
import com.example.adyendemo.module.network.NetworkResult
import com.example.adyendemo.module.network.NetworkService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PaymentRemoteDataSource(private val networkClient: NetworkService) {
    private suspend fun <T> runWithIoDispatcher(block: suspend () -> Response<T>): NetworkResult<T> {
        return withContext(context = Dispatchers.IO) {
            try {
                val response = block.invoke()
                val responseBody = response.body()!!
                NetworkResult.Success(data = responseBody)
            } catch (e: Exception) {
                NetworkResult.Failure(throwable = e)
            }
        }
    }

    suspend fun getPaymentMethods(
        amount: Long,
        currency: String,
        countryCode: String,
        shopperLocale: String
    ): NetworkResult<GetPaymentMethodsResponse> {
        return runWithIoDispatcher {
            networkClient.getPaymentMethods(
                amount = amount,
                currency = currency,
                countryCode = countryCode,
                shopperLocale = shopperLocale
            )
        }
    }

    suspend fun getClientKey(): NetworkResult<String> {
        return runWithIoDispatcher {
            networkClient.getClientKey()
        }
    }

    suspend fun submitPayment(
        request: PaymentRequest
    ): NetworkResult<Map<String, Any>> {
        return runWithIoDispatcher {
            networkClient.initiate(request = request)
        }
    }

    suspend fun processDetails(
        details: Map<String, Any>
    ): NetworkResult<Map<String, Any>> {
        return runWithIoDispatcher {
            networkClient.processDetails(details = details)
        }
    }
}