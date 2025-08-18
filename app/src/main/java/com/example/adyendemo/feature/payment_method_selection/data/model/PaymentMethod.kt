package com.example.adyendemo.feature.payment_method_selection.data.model


import com.google.gson.annotations.SerializedName

data class PaymentMethod(
    @SerializedName("brands")
    val brands: List<String>,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String
) {
    val adyenPaymentMethod: com.adyen.checkout.components.core.PaymentMethod
        get() {
            return com.adyen.checkout.components.core.PaymentMethod(
                brands = this.brands,
                name = this.name,
                type = this.type
            )
        }
}