package com.nextmall.order.application.result

import com.nextmall.order.domain.Order

data class CreateOrderResult(
    val orderId: Long
) {
    constructor(order: Order) : this(order.id)
}
