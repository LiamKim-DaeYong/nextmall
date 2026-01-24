package com.nextmall.checkout.infrastructure.persistence.jooq

import com.nextmall.checkout.application.query.CheckoutSummaryView
import com.nextmall.checkout.application.query.CheckoutView
import com.nextmall.checkout.domain.model.*
import com.nextmall.common.data.jooq.JooqRepository
import com.nextmall.common.data.jooq.util.getRequired
import com.nextmall.jooq.tables.references.CHECKOUTS
import com.nextmall.jooq.tables.references.CHECKOUT_LINE_ITEMS
import com.nextmall.jooq.tables.references.CHECKOUT_PAYMENT_HANDLERS
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class CheckoutJooqRepository(
    dsl: DSLContext,
) : JooqRepository(dsl) {
    private val checkoutFields =
        arrayOf(
            CHECKOUTS.CHECKOUT_ID,
            CHECKOUTS.STATUS,
            CHECKOUTS.CURRENCY,
            CHECKOUTS.SUBTOTAL_AMOUNT,
            CHECKOUTS.TAX_AMOUNT,
            CHECKOUTS.SHIPPING_AMOUNT,
            CHECKOUTS.DISCOUNT_AMOUNT,
            CHECKOUTS.TOTAL_AMOUNT,
            CHECKOUTS.TERMS_URL,
            CHECKOUTS.PRIVACY_URL,
            CHECKOUTS.REFUND_URL,
            CHECKOUTS.EXPIRES_AT,
            CHECKOUTS.PAYMENT_TYPE,
            CHECKOUTS.PAYMENT_PROVIDER,
            CHECKOUTS.BUYER_ID,
            CHECKOUTS.BUYER_EMAIL,
            CHECKOUTS.BUYER_NAME,
            CHECKOUTS.SHIPPING_LINE1,
            CHECKOUTS.SHIPPING_LINE2,
            CHECKOUTS.SHIPPING_CITY,
            CHECKOUTS.SHIPPING_REGION,
            CHECKOUTS.SHIPPING_POSTAL_CODE,
            CHECKOUTS.SHIPPING_COUNTRY,
            CHECKOUTS.BILLING_LINE1,
            CHECKOUTS.BILLING_LINE2,
            CHECKOUTS.BILLING_CITY,
            CHECKOUTS.BILLING_REGION,
            CHECKOUTS.BILLING_POSTAL_CODE,
            CHECKOUTS.BILLING_COUNTRY,
        )

    fun findById(checkoutId: String): CheckoutView? {
        val checkout =
            dsl.select(*checkoutFields)
                .from(CHECKOUTS)
                .where(CHECKOUTS.CHECKOUT_ID.eq(checkoutId))
                .fetchOne() ?: return null

        val lineItems =
            dsl.select(
                CHECKOUT_LINE_ITEMS.LINE_ITEM_ID,
                CHECKOUT_LINE_ITEMS.TITLE,
                CHECKOUT_LINE_ITEMS.QUANTITY,
                CHECKOUT_LINE_ITEMS.PRICE_AMOUNT,
                CHECKOUT_LINE_ITEMS.PRICE_CURRENCY,
                CHECKOUT_LINE_ITEMS.IMAGE_URL,
            )
                .from(CHECKOUT_LINE_ITEMS)
                .where(CHECKOUT_LINE_ITEMS.CHECKOUT_ID.eq(checkoutId))
                .fetch { it.toLineItem() }

        val paymentHandlers =
            dsl.select(
                CHECKOUT_PAYMENT_HANDLERS.HANDLER_TYPE,
                CHECKOUT_PAYMENT_HANDLERS.HANDLER_PROVIDER,
                CHECKOUT_PAYMENT_HANDLERS.SORT_ORDER,
            )
                .from(CHECKOUT_PAYMENT_HANDLERS)
                .where(CHECKOUT_PAYMENT_HANDLERS.CHECKOUT_ID.eq(checkoutId))
                .orderBy(CHECKOUT_PAYMENT_HANDLERS.SORT_ORDER.asc())
                .fetch {
                    PaymentHandler(
                        type = it.getRequired(CHECKOUT_PAYMENT_HANDLERS.HANDLER_TYPE),
                        provider = it[CHECKOUT_PAYMENT_HANDLERS.HANDLER_PROVIDER],
                    )
                }

        return checkout.toCheckoutView(lineItems, paymentHandlers)
    }

    fun findAllSummaries(
        limit: Int,
        offset: Int,
    ): List<CheckoutSummaryView> =
        dsl.select(
            CHECKOUTS.CHECKOUT_ID,
            CHECKOUTS.STATUS,
            CHECKOUTS.CURRENCY,
            CHECKOUTS.TOTAL_AMOUNT,
        )
            .from(CHECKOUTS)
            .orderBy(CHECKOUTS.CREATED_AT.desc())
            .limit(limit)
            .offset(offset)
            .fetch {
                CheckoutSummaryView(
                    id = it.getRequired(CHECKOUTS.CHECKOUT_ID),
                    status = CheckoutStatus.valueOf(it.getRequired(CHECKOUTS.STATUS)),
                    currency = it.getRequired(CHECKOUTS.CURRENCY),
                    totalAmount = it.getRequired(CHECKOUTS.TOTAL_AMOUNT),
                )
            }

    private fun Record.toLineItem(): LineItem =
        LineItem(
            id = getRequired(CHECKOUT_LINE_ITEMS.LINE_ITEM_ID),
            title = getRequired(CHECKOUT_LINE_ITEMS.TITLE),
            quantity = getRequired(CHECKOUT_LINE_ITEMS.QUANTITY),
            price =
                Money(
                    amount = getRequired(CHECKOUT_LINE_ITEMS.PRICE_AMOUNT),
                    currency = getRequired(CHECKOUT_LINE_ITEMS.PRICE_CURRENCY),
                ),
            imageUrl = this[CHECKOUT_LINE_ITEMS.IMAGE_URL],
        )

    private fun Record.toCheckoutView(
        lineItems: List<LineItem>,
        paymentHandlers: List<PaymentHandler>,
    ): CheckoutView {
        val currency = getRequired(CHECKOUTS.CURRENCY)
        val handlers =
            paymentHandlers.ifEmpty {
                val paymentType = this[CHECKOUTS.PAYMENT_TYPE]
                if (paymentType != null) {
                    listOf(
                        PaymentHandler(
                            type = paymentType,
                            provider = this[CHECKOUTS.PAYMENT_PROVIDER],
                        ),
                    )
                } else {
                    emptyList()
                }
            }
        return CheckoutView(
            id = getRequired(CHECKOUTS.CHECKOUT_ID),
            status = CheckoutStatus.valueOf(getRequired(CHECKOUTS.STATUS)),
            currency = currency,
            lineItems = lineItems,
            buyer =
                Buyer(
                    id = this[CHECKOUTS.BUYER_ID],
                    email = this[CHECKOUTS.BUYER_EMAIL],
                    name = this[CHECKOUTS.BUYER_NAME],
                ),
            shippingAddress =
                Address(
                    line1 = this[CHECKOUTS.SHIPPING_LINE1],
                    line2 = this[CHECKOUTS.SHIPPING_LINE2],
                    city = this[CHECKOUTS.SHIPPING_CITY],
                    region = this[CHECKOUTS.SHIPPING_REGION],
                    postalCode = this[CHECKOUTS.SHIPPING_POSTAL_CODE],
                    country = this[CHECKOUTS.SHIPPING_COUNTRY],
                ),
            billingAddress =
                Address(
                    line1 = this[CHECKOUTS.BILLING_LINE1],
                    line2 = this[CHECKOUTS.BILLING_LINE2],
                    city = this[CHECKOUTS.BILLING_CITY],
                    region = this[CHECKOUTS.BILLING_REGION],
                    postalCode = this[CHECKOUTS.BILLING_POSTAL_CODE],
                    country = this[CHECKOUTS.BILLING_COUNTRY],
                ),
            totals =
                Totals(
                    subtotal = Money(amount = getRequired(CHECKOUTS.SUBTOTAL_AMOUNT), currency = currency),
                    tax = Money(amount = getRequired(CHECKOUTS.TAX_AMOUNT), currency = currency),
                    shipping = Money(amount = getRequired(CHECKOUTS.SHIPPING_AMOUNT), currency = currency),
                    discount = Money(amount = getRequired(CHECKOUTS.DISCOUNT_AMOUNT), currency = currency),
                    total = Money(amount = getRequired(CHECKOUTS.TOTAL_AMOUNT), currency = currency),
                ),
            messages = emptyList(),
            links =
                Links(
                    terms = this[CHECKOUTS.TERMS_URL],
                    privacy = this[CHECKOUTS.PRIVACY_URL],
                    refund = this[CHECKOUTS.REFUND_URL],
                ),
            expiresAt = this[CHECKOUTS.EXPIRES_AT]?.toInstant(),
            payment =
                Payment(
                    handlers = handlers,
                ),
        )
    }
}
