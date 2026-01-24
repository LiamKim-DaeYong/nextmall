package com.nextmall.checkout.infrastructure.persistence.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "checkout_line_items")
class CheckoutLineItemEntity(
    @Id
    val id: Long,

    @Column(name = "line_item_id", nullable = false, length = 64)
    val lineItemId: String,

    @Column(nullable = false, length = 255)
    val title: String,

    @Column(nullable = false)
    val quantity: Int,

    @Column(name = "price_amount", nullable = false)
    val priceAmount: Long,

    @Column(name = "price_currency", nullable = false, length = 3)
    val priceCurrency: String,

    @Column(name = "image_url")
    val imageUrl: String?,
)
