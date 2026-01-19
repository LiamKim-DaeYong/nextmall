package com.nextmall.bff.application.order.command

interface CreateOrderFacade {
    suspend fun createOrder(command: CreateOrderCommand): CreateOrderResult
}
