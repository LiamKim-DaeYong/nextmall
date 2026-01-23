package com.nextmall.checkout.infrastructure.persistence.jpa

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "checkout_payment_handlers")
class CheckoutPaymentHandlerEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "handler_type", nullable = false, length = 32)
    val type: String,

    @Column(name = "handler_provider", length = 64)
    val provider: String?,

    @Column(name = "sort_order", nullable = false)
    val sortOrder: Int,
)
