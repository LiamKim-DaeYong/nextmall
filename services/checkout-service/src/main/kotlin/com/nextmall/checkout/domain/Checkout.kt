package com.nextmall.checkout.domain

import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Message
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.Totals
import java.time.Instant

data class Checkout(
    val id: String,
    val status: CheckoutStatus,
    val currency: String,
    val lineItems: List<LineItem>,
    val totals: Totals,
    val messages: List<Message>,
    val links: Links,
    val expiresAt: Instant?,
    val payment: Payment,
)
