package com.example.adyendemo.feature.payment_method_selection.presentation.view

import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardComponentState
import com.adyen.checkout.card.card
import com.adyen.checkout.components.compose.AdyenComponent
import com.adyen.checkout.components.core.ActionComponentData
import com.adyen.checkout.components.core.CheckoutConfiguration
import com.adyen.checkout.components.core.ComponentCallback
import com.adyen.checkout.components.core.ComponentError
import com.adyen.checkout.components.core.PaymentMethod
import com.adyen.checkout.components.core.action.Action
import com.adyen.checkout.core.Environment

@Composable
fun CreditCardPayment(
    modifier: Modifier = Modifier,
    paymentMethod: PaymentMethod,
    reference: String,
    clientKey: String,
    onSubmit: (CardComponentState) -> Unit,
    onProcessDetails: (ActionComponentData) -> Unit,
    onError: (ComponentError) -> Unit,
    action: Action? = null,
    onActionHandled: (() -> Unit),
    intent: Intent?,
    onIntentHandled: () -> Unit
) {
    val activity = LocalActivity.current as? FragmentActivity
    if (activity == null) return
    val checkoutConfiguration = CheckoutConfiguration(
        clientKey = clientKey,
        environment = Environment.TEST
    ) {
        card {
            isHolderNameRequired = true
            isStorePaymentFieldVisible = false
        }
    }
    val cardComponent = CardComponent.PROVIDER.get(
        activity = activity,
        paymentMethod = paymentMethod,
        checkoutConfiguration = checkoutConfiguration,
        callback = object : ComponentCallback<CardComponentState> {
            override fun onAdditionalDetails(actionComponentData: ActionComponentData) {
                onProcessDetails.invoke(actionComponentData)
            }

            override fun onError(componentError: ComponentError) {
                onError.invoke(componentError)
            }

            override fun onSubmit(state: CardComponentState) {
                onSubmit.invoke(state)
            }
        },
        key = reference
    )
    LaunchedEffect(key1 = action) {
        action?.let {
            cardComponent.handleAction(action = it, activity = activity)
            onActionHandled.invoke()
        }
    }
    LaunchedEffect(key1 = intent) {
        if (intent != null) {
            cardComponent.handleIntent(intent = intent)
            onIntentHandled.invoke()
        }
    }
    AdyenComponent(
        component = cardComponent,
        modifier = modifier
    )
}