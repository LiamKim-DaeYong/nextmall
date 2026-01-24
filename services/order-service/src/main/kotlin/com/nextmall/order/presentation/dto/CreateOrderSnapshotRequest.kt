package com.nextmall.order.presentation.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty

data class CreateOrderSnapshotRequest(
    @field:NotBlank
    val checkoutId: String,

    @field:NotEmpty
    @field:Valid
    val lineItems: List<OrderLineItemRequest>,

    @field:Valid
    val totals: OrderTotalsRequest,

    @field:NotBlank
    val currency: String,

    val permalinkUrl: String? = null,
)
