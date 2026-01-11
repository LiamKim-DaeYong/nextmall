package com.nextmall.product.domain

import com.nextmall.common.data.jpa.BaseEntity
import com.nextmall.common.util.Money
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "product_id", nullable = false)
    val id: Long,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(nullable = false, precision = 10, scale = 2)
    private var priceAmount: BigDecimal,

    @Column(nullable = false)
    var stock: Int,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(length = 100)
    var category: String? = null,
) : BaseEntity() {
    var price: Money
        get() = Money.of(priceAmount)
        set(value) {
            priceAmount = value.amount
        }

    fun update(
        name: String,
        price: Money,
        stock: Int,
        category: String?,
    ) {
        this.name = name
        this.price = price
        this.stock = stock
        this.category = category
    }
}
