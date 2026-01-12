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

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private var priceAmount: BigDecimal,

    @Column(name = "stock", nullable = false)
    private var stockAmount: Int,

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

    val stock: Int
        get() = stockAmount

    fun decreaseStock(amount: Int) {
        require(amount > 0) { "Amount must be positive" }
        require(stockAmount >= amount) { "Insufficient stock" }
        stockAmount -= amount
    }

    fun increaseStock(amount: Int) {
        require(amount > 0) { "Amount must be positive" }
        stockAmount += amount
    }

    fun update(
        name: String,
        price: Money,
        stock: Int,
        category: String?,
    ) {
        require(stock >= 0) { "Stock cannot be negative" }
        this.name = name
        this.price = price
        this.stockAmount = stock
        this.category = category
    }
}
