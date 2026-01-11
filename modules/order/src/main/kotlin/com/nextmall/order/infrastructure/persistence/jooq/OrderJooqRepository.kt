package com.nextmall.order.infrastructure.persistence.jooq

import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.common.util.Money
import com.nextmall.jooq.tables.references.ORDERS
import com.nextmall.order.application.query.OrderView
import com.nextmall.order.domain.model.OrderStatus
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class OrderJooqRepository(
    dsl: DSLContext
) : JooqRepository(dsl) {

    private val fields = arrayOf(
        ORDERS.ORDER_ID,
        ORDERS.USER_ID,
        ORDERS.PRODUCT_ID,
        ORDERS.QUANTITY,
        ORDERS.TOTAL_PRICE,
        ORDERS.STATUS
    )

    fun findById(orderId: Long): OrderView? =
        dsl.select(*fields)
            .from(ORDERS)
            .where(ORDERS.ORDER_ID.eq(orderId))
            .fetchOne { it.toOrderView() }

    fun findAllByUserId(userId: Long): List<OrderView> =
        dsl.select(*fields)
            .from(ORDERS)
            .where(ORDERS.USER_ID.eq(userId))
            .fetch { it.toOrderView() }

    private fun Record.toOrderView() =
        OrderView(
            id = getRequired(ORDERS.ORDER_ID),
            userId = getRequired(ORDERS.USER_ID),
            productId = getRequired(ORDERS.PRODUCT_ID),
            quantity = getRequired(ORDERS.QUANTITY),
            totalPrice = Money.of(getRequired(ORDERS.TOTAL_PRICE)),
            status = OrderStatus.valueOf(getRequired(ORDERS.STATUS))
        )
}
