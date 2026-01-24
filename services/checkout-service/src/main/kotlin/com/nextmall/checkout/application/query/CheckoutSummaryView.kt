package com.nextmall.checkout.application.query

import com.nextmall.checkout.domain.model.CheckoutStatus

data class CheckoutSummaryView(
    val id: String,
    val status: CheckoutStatus,
    val currency: String,
    val totalAmount: Long,
)
