package com.example.adyendemo.feature.payment_method_selection.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adyen.checkout.card.CardComponentState
import com.adyen.checkout.components.core.ActionComponentData
import com.adyen.checkout.components.core.ComponentError
import com.adyen.checkout.components.core.PaymentComponentData
import com.adyen.checkout.components.core.action.Action
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentRequest
import com.example.adyendemo.feature.payment_method_selection.domain.repository.PaymentRepository
import com.example.adyendemo.module.AppConstants.REDIRECT_URL
import com.example.adyendemo.module.network.onFailure
import com.example.adyendemo.module.network.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class PaymentDetailViewModel(private val paymentRepository: PaymentRepository) : ViewModel() {
    private val _state = MutableStateFlow(value = PaymentDetailState())
    val state = _state.asStateFlow()

    private fun runFunction(function: suspend () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            function.invoke()
        }.invokeOnCompletion {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    @OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
    private fun createReference(): String {
        val timestamp = Clock.System.now()
        return "${Uuid.random()}_${timestamp.toEpochMilliseconds()}"
    }

    private fun getReference() {
        _state.value = _state.value.copy(reference = createReference())
    }

    private fun fetchPaymentInfo() {
        runFunction {
            getReference()
            getClientKey()
        }
    }

    private suspend fun getClientKey() {
        paymentRepository.getClientKey().onSuccess { clientKey ->
            _state.value = _state.value.copy(clientKey = clientKey)
        }.onFailure { exception ->
            _state.value = _state.value.copy(errorMessage = exception.message)
        }
    }

    private fun resetErrorMessage() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    fun retryFetchPaymentInfo() {
        runFunction {
            resetErrorMessage()

            getReference()
            getClientKey()
        }
    }

    private fun jsonObjectToMap(jsonObject: JSONObject): Map<String, Any> {
        val map = mutableMapOf<String, Any>()
        val keys = jsonObject.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val value = jsonObject.get(key)

            when (value) {
                is JSONObject -> map[key] =
                    jsonObjectToMap(value) // Recursively convert nested JSONObject
                is JSONArray -> map[key] = jsonArrayToList(value) // Convert JSONArray to List
                else -> map[key] = value // Add primitive types directly
            }
        }
        return map
    }

    private fun jsonArrayToList(jsonArray: JSONArray): List<Any> {
        val list = mutableListOf<Any>()
        for (i in 0 until jsonArray.length()) {
            val value = jsonArray.get(i)
            when (value) {
                is JSONObject -> list.add(jsonObjectToMap(value)) // Recursively convert nested JSONObject
                is JSONArray -> list.add(jsonArrayToList(value)) // Convert nested JSONArray to List
                else -> list.add(value) // Add primitive types directly
            }
        }
        return list
    }

    private fun processResponse(response: Map<String, Any>) {
        try {
            val jsonObject = JSONObject(response)
            val actionJsonObject = jsonObject.optJSONObject(ACTION_KEY)
            if (actionJsonObject != null) {
                val action = Action.SERIALIZER.deserialize(actionJsonObject)
                _state.value = _state.value.copy(action = action)
            } else {
                val resultCode = jsonObject.optString(RESULT_CODE_KEY)
                if (resultCode == AUTHORISED_KEY) {
                    _state.value = _state.value.copy(isAuthorized = true)
                } else {
                    _state.value = _state.value.copy(errorMessage = response.toString())
                }
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message)
            return
        }
    }

    fun submitPayment(
        state: CardComponentState,
        paymentLocale: PaymentLocale,
        amount: Long,
        reference: String
    ) {
        runFunction {
            val paymentMethod = try {
                val jsonObject = PaymentComponentData
                    .SERIALIZER
                    .serialize(state.data)
                    .get(PAYMENT_METHOD_JSON_OBJECT_KEY) as JSONObject
                jsonObjectToMap(jsonObject)
            } catch (_: Exception) {
                return@runFunction
            }
            val userAgent = try {
                System.getProperty("http.agent")
            } catch (_: Exception) {
                return@runFunction
            }
            paymentRepository.submitPayment(
                request = PaymentRequest(
                    amount = amount,
                    currency = paymentLocale.currency,
                    reference = reference,
                    redirectUrl = REDIRECT_URL,
                    channel = ANDROID_CHANNEL,
                    paymentMethod = paymentMethod,
                    browserInfo = PaymentRequest.BrowserInfo(
                        userAgent = userAgent,
                        acceptHeader = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
                    )
                )
            ).onSuccess { response ->
                processResponse(response = response)
            }.onFailure { exception ->
                _state.value = _state.value.copy(errorMessage = exception.message)
            }
        }
    }

    fun processDetails(actionComponentData: ActionComponentData) {
        runFunction {
            val details = try {
                val jsonObject = ActionComponentData.SERIALIZER.serialize(actionComponentData)
                jsonObjectToMap(jsonObject = jsonObject)
            } catch (_: Exception) {
                return@runFunction
            }
            paymentRepository.processDetails(details = details).onSuccess { response ->
                processResponse(response = response)
            }.onFailure { exception ->
                _state.value = _state.value.copy(errorMessage = exception.message)
            }
        }
    }

    fun onActionHandled() {
        _state.value = _state.value.copy(action = null)
    }

    fun setError(error: ComponentError) {
        _state.value = _state.value.copy(errorMessage = error.errorMessage)
    }

    init {
        fetchPaymentInfo()
    }

    companion object {
        private const val ANDROID_CHANNEL = "Android"
        private const val PAYMENT_METHOD_JSON_OBJECT_KEY = "paymentMethod"
        private const val ACTION_KEY = "action"

        private const val RESULT_CODE_KEY = "resultCode"
        private const val AUTHORISED_KEY = "Authorised"
    }
}