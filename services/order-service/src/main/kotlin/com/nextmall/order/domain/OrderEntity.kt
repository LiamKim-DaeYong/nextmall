package com.nextmall.order.domain

import com.nextmall.common.data.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @Column(name = "order_id", nullable = false)
    val id: Long,

    @Column(name = "checkout_id", nullable = false, length = 64)
    val checkoutId: String,

    @Column(name = "currency", nullable = false, length = 10)
    val currency: String,

    @Column(name = "permalink_url", columnDefinition = "TEXT")
    val permalinkUrl: String? = null,

    @Lob
    @Column(name = "line_items_json", nullable = false, columnDefinition = "TEXT")
    val lineItemsJson: String,

    @Lob
    @Column(name = "totals_json", nullable = false, columnDefinition = "TEXT")
    val totalsJson: String,

    @Lob
    @Column(name = "fulfillment_json", nullable = false, columnDefinition = "TEXT")
    val fulfillmentJson: String,

    @Lob
    @Column(name = "adjustments_json", nullable = false, columnDefinition = "TEXT")
    val adjustmentsJson: String,
) : BaseEntity()
// Phase 1: store UCP-like snapshots as JSON. Phase 2 may normalize to line_items/adjustments tables.
