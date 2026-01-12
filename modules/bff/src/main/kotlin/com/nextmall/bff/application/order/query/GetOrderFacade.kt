package com.nextmall.bff.application.order.query

interface GetOrderFacade {
    suspend fun getOrder(orderId: Long): OrderViewResult

    suspend fun getOrdersByUserId(userId: Long): List<OrderViewResult>
}
