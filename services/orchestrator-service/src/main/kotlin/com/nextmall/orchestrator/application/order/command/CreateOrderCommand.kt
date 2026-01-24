package com.nextmall.orchestrator.application.order.command

data class CreateOrderCommand(
    val userId: Long,
    val productId: Long,
    val quantity: Int,
)
