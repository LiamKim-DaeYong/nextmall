package com.nextmall.bff.application.order.query

import reactor.core.publisher.Mono

interface GetOrderFacade {
    fun getOrder(orderId: Long): Mono<OrderViewResult>

    fun getOrdersByUserId(userId: Long): Mono<List<OrderViewResult>>
}
