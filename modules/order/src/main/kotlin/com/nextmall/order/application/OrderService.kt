package com.nextmall.order.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.util.Money
import com.nextmall.order.application.query.OrderView
import com.nextmall.order.application.result.CreateOrderResult
import com.nextmall.order.domain.Order
import com.nextmall.order.domain.exception.OrderNotFoundException
import com.nextmall.order.domain.model.OrderStatus
import com.nextmall.order.infrastructure.persistence.jooq.OrderJooqRepository
import com.nextmall.order.infrastructure.persistence.jpa.OrderJpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class OrderService(
    private val idGenerator: IdGenerator,
    private val orderJpaRepository: OrderJpaRepository,
    private val orderJooqRepository: OrderJooqRepository,
) {
    @Transactional(readOnly = true)
    fun getOrder(orderId: Long): OrderView =
        orderJooqRepository.findById(orderId)
            ?: throw OrderNotFoundException()

    @Transactional(readOnly = true)
    fun getOrdersByUserId(userId: Long): List<OrderView> = orderJooqRepository.findAllByUserId(userId)

    @Transactional
    fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        totalPrice: Money,
    ): CreateOrderResult {
        val order =
            Order(
                id = idGenerator.generate(),
                userId = userId,
                productId = productId,
                quantity = quantity,
                totalPriceAmount = totalPrice.amount,
                status = OrderStatus.PENDING,
            )

        val saved = orderJpaRepository.save(order)
        return CreateOrderResult(saved)
    }

    @Transactional
    fun cancelOrder(orderId: Long) {
        val order =
            orderJpaRepository
                .findById(orderId)
                .orElseThrow { OrderNotFoundException() }

        order.cancel()
    }
}
