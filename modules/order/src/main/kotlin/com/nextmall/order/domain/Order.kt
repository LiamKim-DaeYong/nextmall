package com.nextmall.order.domain

import com.nextmall.common.data.jpa.BaseEntity
import com.nextmall.common.util.Money
import com.nextmall.order.domain.model.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "order_id", nullable = false)
    val id: Long,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "product_id", nullable = false)
    val productId: Long,

    @Column(nullable = false)
    var quantity: Int,

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private var totalPriceAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: OrderStatus
) : BaseEntity() {
    var totalPrice: Money
        get() = Money.of(totalPriceAmount)
        set(value) {
            totalPriceAmount = value.amount
        }

    fun cancel() {
        require(status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED) {
            "Only PENDING or CONFIRMED orders can be cancelled"
        }
        this.status = OrderStatus.CANCELLED
    }

    fun confirm() {
        require(status == OrderStatus.PENDING) {
            "Only PENDING orders can be confirmed"
        }
        this.status = OrderStatus.CONFIRMED
    }

    fun complete() {
        require(status == OrderStatus.CONFIRMED) {
            "Only CONFIRMED orders can be completed"
        }
        this.status = OrderStatus.COMPLETED
    }
}
