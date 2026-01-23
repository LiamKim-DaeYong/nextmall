package com.nextmall.checkout.application.command

data class CreateCheckoutCommand(
    val lineItems: List<LineItemCommand>,
    val currency: String,
    val buyer: BuyerCommand?,
    val returnUrl: String?,
    val cancelUrl: String?,
)

data class UpdateCheckoutCommand(
    val lineItems: List<LineItemCommand>?,
    val buyer: BuyerCommand?,
    val shippingAddress: AddressCommand?,
    val billingAddress: AddressCommand?,
)

data class CompleteCheckoutCommand(
    val payment: PaymentCommand,
    val confirm: Boolean?,
)
