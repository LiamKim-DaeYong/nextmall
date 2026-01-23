package com.nextmall.checkout.presentation.response

data class CheckoutResponse(
    val id: String,
    val status: String,
    val currency: String,
    val lineItems: List<LineItemResponse>,
    val buyer: BuyerResponse?,
    val shippingAddress: AddressResponse?,
    val billingAddress: AddressResponse?,
    val totals: TotalsResponse,
    val messages: List<MessageResponse>,
    val links: LinksResponse,
    val expiresAt: String?,
    val payment: PaymentResponse,
)
