package com.example.adyendemo

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.util.Consumer
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import androidx.navigation.toRoute
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentLocale
import com.example.adyendemo.feature.payment_method_selection.data.model.PaymentMethod
import com.example.adyendemo.feature.payment_method_selection.presentation.screen.PaymentDetailScreen
import com.example.adyendemo.feature.payment_method_selection.presentation.screen.PaymentMethodSelectionScreen
import com.example.adyendemo.feature.payment_method_selection.presentation.screen.PaymentSuccessScreen
import com.example.adyendemo.ui.theme.AdyenDemoTheme
import com.google.gson.Gson
import kotlinx.serialization.Serializable
import org.koin.android.ext.android.inject

@Serializable
object PaymentMethodSelection

@Serializable
data class PaymentDetail(
    val paymentLocaleJsonString: String,
    val paymentMethodJsonString: String,
    val amount: Long
)

@Serializable
object PaymentSuccess

class MainActivity : FragmentActivity() {
    private val gson by inject<Gson>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdyenDemoTheme {
                val navController = rememberNavController()
                var intent by remember {
                    mutableStateOf<Intent?>(null)
                }
                DisposableEffect(key1 = navController) {
                    val consumer = Consumer<Intent> {
                        intent = it
                    }
                    this@MainActivity.addOnNewIntentListener(consumer)
                    onDispose {
                        this@MainActivity.removeOnNewIntentListener(consumer)
                    }
                }
                NavHost(
                    navController = navController,
                    startDestination = PaymentMethodSelection
                ) {
                    composable<PaymentMethodSelection> {
                        PaymentMethodSelectionScreen(
                            modifier = Modifier
                                .fillMaxSize(),
                            onPaymentDetailRequested = { paymentLocale, paymentMethod, amount ->
                                val paymentLocaleJsonString = gson.toJson(paymentLocale)
                                val paymentMethodJsonString = gson.toJson(paymentMethod)
                                navController.navigate(
                                    route = PaymentDetail(
                                        paymentLocaleJsonString = paymentLocaleJsonString,
                                        paymentMethodJsonString = paymentMethodJsonString,
                                        amount = amount
                                    ),
                                    navOptions = navOptions {
                                        launchSingleTop = true
                                    }
                                )
                            }
                        )
                    }
                    composable<PaymentDetail> {
                        val paymentLocaleJsonString =
                            it.toRoute<PaymentDetail>().paymentLocaleJsonString
                        val paymentLocale = try {
                            gson.fromJson(paymentLocaleJsonString, PaymentLocale::class.java)
                        } catch (_: Exception) {
                            return@composable
                        }
                        val paymentMethodJsonString =
                            it.toRoute<PaymentDetail>().paymentMethodJsonString
                        val paymentMethod = try {
                            gson.fromJson(paymentMethodJsonString, PaymentMethod::class.java)
                        } catch (_: Exception) {
                            return@composable
                        }
                        val amount = it.toRoute<PaymentDetail>().amount
                        PaymentDetailScreen(
                            modifier = Modifier
                                .fillMaxSize(),
                            paymentLocale = paymentLocale,
                            paymentMethod = paymentMethod,
                            amount = amount,
                            intent = intent,
                            onIntentHandled = {
                                intent = null
                            },
                            onPaymentSuccessRequested = {
                                navController.navigate(
                                    route = PaymentSuccess,
                                    navOptions = navOptions {
                                        launchSingleTop = true
                                    }
                                )
                            }
                        )
                    }
                    composable<PaymentSuccess> {
                        PaymentSuccessScreen(
                            modifier = Modifier
                                .fillMaxSize(),
                            onPaymentMethodSelectionRequested = {
                                navController.popBackStack(
                                    route = PaymentMethodSelection,
                                    inclusive = false
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}