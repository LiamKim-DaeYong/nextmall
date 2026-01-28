package com.nextmall.order.application

import com.nextmall.common.identifier.IdGenerator
import com.nextmall.common.kafka.event.order.OrderCreatedEvent
import com.nextmall.common.kafka.producer.EventPublisher
import com.nextmall.order.domain.OrderEntity
import com.nextmall.order.domain.OrderStatus
import com.nextmall.order.infrastructure.persistence.jpa.OrderJpaRepository
import com.nextmall.order.presentation.dto.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper

@Service
class OrderService(
    private val idGenerator: IdGenerator,
    private val orderJpaRepository: OrderJpaRepository,
    private val objectMapper: ObjectMapper,
    private val eventPublisher: EventPublisher,
) {
    private val lineItemsType = object : TypeReference<List<OrderLineItem>>() {}
    private val adjustmentsType = object : TypeReference<List<Map<String, Any>>>() {}

    fun getOrder(orderId: Long): OrderSnapshot =
        orderJpaRepository
            .findById(orderId)
            .map { it.toSnapshot() }
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: $orderId") }

    @Transactional
    fun createOrder(request: CreateOrderSnapshotRequest): OrderSnapshot {
        val id = idGenerator.generate()
        // Phase 1: accept checkout snapshot as-is. Validation/normalization happens in upper layers later.
        val lineItems =
            request.lineItems.map {
                OrderLineItem(
                    id = it.id,
                    title = it.title,
                    quantity = it.quantity,
                    price = MoneyAmount(it.price.amount, it.price.currency),
                    imageUrl = it.imageUrl,
                )
            }
        val totals =
            OrderTotals(
                subtotal = MoneyAmount(request.totals.subtotal.amount, request.totals.subtotal.currency),
                tax = MoneyAmount(request.totals.tax.amount, request.totals.tax.currency),
                shipping = MoneyAmount(request.totals.shipping.amount, request.totals.shipping.currency),
                discount = MoneyAmount(request.totals.discount.amount, request.totals.discount.currency),
                total = MoneyAmount(request.totals.total.amount, request.totals.total.currency),
            )
        val fulfillment = OrderFulfillment()
        val adjustments = emptyList<Map<String, Any>>()

        val entity =
            OrderEntity(
                id = id,
                checkoutId = request.checkoutId,
                currency = request.currency,
                permalinkUrl = request.permalinkUrl,
                lineItemsJson = objectMapper.writeValueAsString(lineItems),
                totalsJson = objectMapper.writeValueAsString(totals),
                fulfillmentJson = objectMapper.writeValueAsString(fulfillment),
                adjustmentsJson = objectMapper.writeValueAsString(adjustments),
                status = OrderStatus.PENDING,
            )

        val saved = orderJpaRepository.save(entity)
        saved.confirm()
        publishOrderCreatedEvent(saved.id, lineItems)
        return saved.toSnapshot()
    }

    private fun OrderEntity.toSnapshot(): OrderSnapshot {
        val lineItems = objectMapper.readValue(lineItemsJson, lineItemsType)
        val totals = objectMapper.readValue(totalsJson, OrderTotals::class.java)
        val fulfillment = objectMapper.readValue(fulfillmentJson, OrderFulfillment::class.java)
        val adjustments = objectMapper.readValue(adjustmentsJson, adjustmentsType)

        return OrderSnapshot(
            orderId = id,
            checkoutId = checkoutId,
            permalinkUrl = permalinkUrl,
            lineItems = lineItems,
            fulfillment = fulfillment,
            adjustments = adjustments,
            totals = totals,
        )
    }

    private fun publishOrderCreatedEvent(
        orderId: Long,
        lineItems: List<OrderLineItem>,
    ) {
        lineItems.forEach { item ->
            val productId =
                item.id.toLongOrNull()
                    ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid productId: ${item.id}")
            eventPublisher.publish(
                topic = ORDER_CREATED_TOPIC,
                event =
                    OrderCreatedEvent(
                        orderId = orderId,
                        productId = productId,
                        quantity = item.quantity,
                    ),
            )
        }
    }

    companion object {
        private const val ORDER_CREATED_TOPIC = "order.created"
    }
}
