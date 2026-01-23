package com.nextmall.checkout.infrastructure.persistence.jpa

import com.nextmall.checkout.domain.Checkout
import com.nextmall.checkout.domain.model.Address
import com.nextmall.checkout.domain.model.Buyer
import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.Money
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.PaymentHandler
import com.nextmall.checkout.domain.model.Totals
import com.nextmall.common.data.jpa.BaseEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "checkouts")
class CheckoutEntity(
    @Id
    @Column(name = "checkout_id", nullable = false, length = 64)
    val id: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    var status: CheckoutStatus,

    @Column(nullable = false, length = 3)
    var currency: String,

    @Column(name = "buyer_id", length = 64)
    var buyerId: String?,

    @Column(name = "buyer_email", length = 255)
    var buyerEmail: String?,

    @Column(name = "buyer_name", length = 255)
    var buyerName: String?,

    @Column(name = "shipping_line1", length = 255)
    var shippingLine1: String?,

    @Column(name = "shipping_line2", length = 255)
    var shippingLine2: String?,

    @Column(name = "shipping_city", length = 100)
    var shippingCity: String?,

    @Column(name = "shipping_region", length = 100)
    var shippingRegion: String?,

    @Column(name = "shipping_postal_code", length = 32)
    var shippingPostalCode: String?,

    @Column(name = "shipping_country", length = 2)
    var shippingCountry: String?,

    @Column(name = "billing_line1", length = 255)
    var billingLine1: String?,

    @Column(name = "billing_line2", length = 255)
    var billingLine2: String?,

    @Column(name = "billing_city", length = 100)
    var billingCity: String?,

    @Column(name = "billing_region", length = 100)
    var billingRegion: String?,

    @Column(name = "billing_postal_code", length = 32)
    var billingPostalCode: String?,

    @Column(name = "billing_country", length = 2)
    var billingCountry: String?,

    @Column(name = "subtotal_amount", nullable = false)
    var subtotalAmount: Long,

    @Column(name = "tax_amount", nullable = false)
    var taxAmount: Long,

    @Column(name = "shipping_amount", nullable = false)
    var shippingAmount: Long,

    @Column(name = "discount_amount", nullable = false)
    var discountAmount: Long,

    @Column(name = "total_amount", nullable = false)
    var totalAmount: Long,

    @Column(name = "terms_url")
    var termsUrl: String?,

    @Column(name = "privacy_url")
    var privacyUrl: String?,

    @Column(name = "refund_url")
    var refundUrl: String?,

    @Column(name = "expires_at")
    var expiresAt: Instant?,

    @Column(name = "payment_type", length = 32)
    var paymentType: String?,

    @Column(name = "payment_provider", length = 64)
    var paymentProvider: String?,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    @JoinColumn(name = "checkout_id")
    val paymentHandlers: MutableList<CheckoutPaymentHandlerEntity>,

    @OneToMany(
        cascade = [CascadeType.ALL],
        orphanRemoval = true,
        fetch = FetchType.LAZY,
    )
    @JoinColumn(name = "checkout_id")
    val lineItems: MutableList<CheckoutLineItemEntity>,
) : BaseEntity() {
    fun toDomain(): Checkout =
        Checkout(
            id = id,
            status = status,
            currency = currency,
            lineItems =
                lineItems.map {
                    LineItem(
                        id = it.lineItemId,
                        title = it.title,
                        quantity = it.quantity,
                        price = Money(amount = it.priceAmount, currency = it.priceCurrency),
                        imageUrl = it.imageUrl,
                    )
                },
            buyer =
                Buyer(
                    id = buyerId,
                    email = buyerEmail,
                    name = buyerName,
                ),
            shippingAddress =
                Address(
                    line1 = shippingLine1,
                    line2 = shippingLine2,
                    city = shippingCity,
                    region = shippingRegion,
                    postalCode = shippingPostalCode,
                    country = shippingCountry,
                ),
            billingAddress =
                Address(
                    line1 = billingLine1,
                    line2 = billingLine2,
                    city = billingCity,
                    region = billingRegion,
                    postalCode = billingPostalCode,
                    country = billingCountry,
                ),
            totals =
                Totals(
                    subtotal = Money(amount = subtotalAmount, currency = currency),
                    tax = Money(amount = taxAmount, currency = currency),
                    shipping = Money(amount = shippingAmount, currency = currency),
                    discount = Money(amount = discountAmount, currency = currency),
                    total = Money(amount = totalAmount, currency = currency),
                ),
            messages = emptyList(),
            links =
                Links(
                    terms = termsUrl,
                    privacy = privacyUrl,
                    refund = refundUrl,
                ),
            expiresAt = expiresAt,
            payment =
                Payment(
                    handlers =
                        paymentHandlers
                            .sortedBy { it.sortOrder }
                            .map { PaymentHandler(type = it.type, provider = it.provider) }
                            .ifEmpty {
                                listOf(
                                    PaymentHandler(
                                        type = paymentType ?: "card",
                                        provider = paymentProvider,
                                    ),
                                )
                            },
                ),
        )
}
