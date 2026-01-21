package com.nextmall.bff.application.order.command

import reactor.core.publisher.Mono

interface CreateOrderFacade {
    fun createOrder(command: CreateOrderCommand): Mono<CreateOrderResult>
}
