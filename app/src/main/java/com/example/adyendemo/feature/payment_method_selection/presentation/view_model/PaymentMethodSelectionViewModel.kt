package com.example.adyendemo.feature.payment_method_selection.presentation.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod
import com.example.adyendemo.feature.payment_method_selection.domain.repository.PaymentRepository
import com.example.adyendemo.module.network.onFailure
import com.example.adyendemo.module.network.onSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentMethodSelectionViewModel(private val paymentRepository: PaymentRepository) :
    ViewModel() {
    private val _state =
        MutableStateFlow(value = PaymentMethodSelectionState(amount = INITIAL_AMOUNT))
    val state = _state.asStateFlow()

    private fun runFunction(function: suspend () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            function.invoke()
        }.invokeOnCompletion {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private fun initiateData() {
        runFunction {
            val paymentLocales = paymentRepository.getPaymentLocales()
            _state.value = _state.value.copy(paymentLocales = paymentLocales)

            val selectedPaymentLocale = paymentLocales.firstOrNull()
            _state.value = _state.value.copy(selectedPaymentLocale = selectedPaymentLocale)

            val amount = INITIAL_AMOUNT
            _state.value = _state.value.copy(amount = amount)

            if (selectedPaymentLocale != null) {
                getPaymentMethods(amount = amount, paymentLocale = selectedPaymentLocale)
            }
        }
    }

    fun setAmount(amount: Long) {
        runFunction {
            val paymentLocale = _state.value.selectedPaymentLocale ?: return@runFunction
            _state.value = _state.value.copy(
                amount = amount
            )

            resetPaymentMethods()
            delay(timeMillis = SET_AMOUNT_DELAY)
            getPaymentMethods(amount = amount, paymentLocale = paymentLocale)
        }
    }

    fun setSelectedPaymentLocale(paymentLocale: PaymentLocale) {
        runFunction {
            val amount = _state.value.amount
            _state.value = _state.value.copy(
                selectedPaymentLocale = paymentLocale
            )

            resetPaymentMethods()
            getPaymentMethods(amount = amount, paymentLocale = paymentLocale)
        }
    }

    fun setSelectedPaymentMethod(paymentMethod: PaymentMethod) {
        runFunction {
            _state.value = _state.value.copy(selectedPaymentMethod = paymentMethod)
        }
    }

    private fun resetPaymentMethods() {
        _state.value = _state.value.copy(data = null)
        _state.value = _state.value.copy(selectedPaymentMethod = null)
    }

    private suspend fun getPaymentMethods(amount: Long, paymentLocale: PaymentLocale) {
        paymentRepository.getPaymentMethods(
            amount = amount,
            currency = paymentLocale.currency,
            countryCode = paymentLocale.countryCode,
            shopperLocale = paymentLocale.shopperLocale
        ).onSuccess {
            _state.value = _state.value.copy(data = it)
        }.onFailure {
            _state.value = _state.value.copy(errorMessage = it.message)
        }
    }

    fun retryGetPaymentMethods() {
        runFunction {
            resetErrorMessage()

            val amount = _state.value.amount
            val paymentLocale = _state.value.selectedPaymentLocale ?: return@runFunction

            resetPaymentMethods()
            getPaymentMethods(amount = amount, paymentLocale = paymentLocale)
        }
    }

    private fun resetErrorMessage() {
        _state.value = _state.value.copy(errorMessage = null)
    }

    init {
        initiateData()
    }

    companion object {
        private const val SET_AMOUNT_DELAY = 2500L
        const val INITIAL_AMOUNT = 100L
    }
}