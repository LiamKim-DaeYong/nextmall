package com.nextmall.checkout.domain

import com.nextmall.checkout.domain.model.Adjustment
import com.nextmall.checkout.domain.model.Fulfillment
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Totals

data class Order(
    val id: String,
    val checkoutId: String,
    val permalinkUrl: String,
    val lineItems: List<LineItem>,
    val fulfillment: Fulfillment,
    val adjustments: List<Adjustment>,
    val totals: Totals,
)
