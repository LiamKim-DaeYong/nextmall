package com.nextmall.product.domain

import com.nextmall.common.data.jpa.BaseEntity
import com.nextmall.common.util.Money
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "products")
class Product(
    @Id
    @Column(name = "product_id", nullable = false)
    val id: Long,

    @Column(nullable = false, length = 200)
    var name: String,

    @Column(length = 2000)
    var description: String? = null,

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private var priceAmount: BigDecimal,

    @Column(name = "stock", nullable = false)
    private var stockAmount: Int,

    @Enumerated(EnumType.STRING)
    @Column(name = "sale_status", nullable = false, length = 20)
    var saleStatus: SaleStatus = SaleStatus.ON_SALE,

    @Enumerated(EnumType.STRING)
    @Column(name = "display_status", nullable = false, length = 20)
    var displayStatus: DisplayStatus = DisplayStatus.VISIBLE,

    @Column(name = "seller_id", nullable = false)
    val sellerId: Long,

    @Column(length = 100)
    var category: String? = null,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: Instant? = null,
) : BaseEntity() {
    @Version
    var version: Long = 0

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

        if (stockAmount == 0 && saleStatus == SaleStatus.ON_SALE) {
            saleStatus = SaleStatus.SOLD_OUT
        }
    }

    fun increaseStock(amount: Int) {
        require(amount > 0) { "Amount must be positive" }
        stockAmount += amount

        if (stockAmount > 0 && saleStatus == SaleStatus.SOLD_OUT) {
            saleStatus = SaleStatus.ON_SALE
        }
    }

    fun update(
        name: String,
        description: String?,
        price: Money,
        stock: Int,
        category: String?,
    ) {
        require(stock >= 0) { "Stock cannot be negative" }
        this.name = name
        this.description = description
        this.price = price
        this.stockAmount = stock
        this.category = category

        if (stockAmount == 0 && saleStatus == SaleStatus.ON_SALE) {
            saleStatus = SaleStatus.SOLD_OUT
        } else if (stockAmount > 0 && saleStatus == SaleStatus.SOLD_OUT) {
            saleStatus = SaleStatus.ON_SALE
        }
    }

    fun suspend() {
        require(!isDeleted) { "Cannot change status of deleted product" }
        require(saleStatus != SaleStatus.SUSPENDED) { "Already suspended" }
        saleStatus = SaleStatus.SUSPENDED
    }

    fun resume() {
        require(!isDeleted) { "Cannot change status of deleted product" }
        require(saleStatus == SaleStatus.SUSPENDED) { "Can only resume from SUSPENDED state" }
        require(stockAmount > 0) { "Cannot resume with zero stock" }
        saleStatus = SaleStatus.ON_SALE
    }

    fun show() {
        require(!isDeleted) { "Cannot change status of deleted product" }
        displayStatus = DisplayStatus.VISIBLE
    }

    fun hide() {
        require(!isDeleted) { "Cannot change status of deleted product" }
        displayStatus = DisplayStatus.HIDDEN
    }

    fun delete() {
        require(!isDeleted) { "Already deleted" }
        require(saleStatus == SaleStatus.SUSPENDED) { "Cannot delete while on sale. Suspend first." }
        isDeleted = true
        deletedAt = Instant.now()
        displayStatus = DisplayStatus.HIDDEN
    }

    fun restore() {
        require(isDeleted) { "Not deleted" }
        isDeleted = false
        deletedAt = null
    }
}
