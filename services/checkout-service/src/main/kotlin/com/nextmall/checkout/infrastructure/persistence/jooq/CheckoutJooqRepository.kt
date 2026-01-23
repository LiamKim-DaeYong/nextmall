package com.nextmall.checkout.infrastructure.persistence.jooq

import com.nextmall.checkout.application.query.CheckoutView
import com.nextmall.checkout.application.query.CheckoutSummaryView
import com.nextmall.checkout.domain.model.Address
import com.nextmall.checkout.domain.model.Buyer
import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.Message
import com.nextmall.checkout.domain.model.Money
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.PaymentHandler
import com.nextmall.checkout.domain.model.Totals
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
                        type = getRequired(CHECKOUT_PAYMENT_HANDLERS.HANDLER_TYPE),
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
                    id = getRequired(CHECKOUTS.CHECKOUT_ID),
                    status = CheckoutStatus.valueOf(getRequired(CHECKOUTS.STATUS)),
                    currency = getRequired(CHECKOUTS.CURRENCY),
                    totalAmount = getRequired(CHECKOUTS.TOTAL_AMOUNT),
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
            imageUrl = it[CHECKOUT_LINE_ITEMS.IMAGE_URL],
        )

    private fun Record.toCheckoutView(
        lineItems: List<LineItem>,
        paymentHandlers: List<PaymentHandler>,
    ): CheckoutView =
        CheckoutView(
            id = getRequired(CHECKOUTS.CHECKOUT_ID),
            status = CheckoutStatus.valueOf(getRequired(CHECKOUTS.STATUS)),
            currency = getRequired(CHECKOUTS.CURRENCY),
            lineItems = lineItems,
            buyer =
                Buyer(
                    id = it[CHECKOUTS.BUYER_ID],
                    email = it[CHECKOUTS.BUYER_EMAIL],
                    name = it[CHECKOUTS.BUYER_NAME],
                ),
            shippingAddress =
                Address(
                    line1 = it[CHECKOUTS.SHIPPING_LINE1],
                    line2 = it[CHECKOUTS.SHIPPING_LINE2],
                    city = it[CHECKOUTS.SHIPPING_CITY],
                    region = it[CHECKOUTS.SHIPPING_REGION],
                    postalCode = it[CHECKOUTS.SHIPPING_POSTAL_CODE],
                    country = it[CHECKOUTS.SHIPPING_COUNTRY],
                ),
            billingAddress =
                Address(
                    line1 = it[CHECKOUTS.BILLING_LINE1],
                    line2 = it[CHECKOUTS.BILLING_LINE2],
                    city = it[CHECKOUTS.BILLING_CITY],
                    region = it[CHECKOUTS.BILLING_REGION],
                    postalCode = it[CHECKOUTS.BILLING_POSTAL_CODE],
                    country = it[CHECKOUTS.BILLING_COUNTRY],
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
                    terms = it[CHECKOUTS.TERMS_URL],
                    privacy = it[CHECKOUTS.PRIVACY_URL],
                    refund = it[CHECKOUTS.REFUND_URL],
                ),
            expiresAt = it[CHECKOUTS.EXPIRES_AT],
            payment =
                Payment(
                    handlers =
                        paymentHandlers.ifEmpty {
                            listOf(
                                PaymentHandler(
                                    type = getRequired(CHECKOUTS.PAYMENT_TYPE),
                                    provider = it[CHECKOUTS.PAYMENT_PROVIDER],
                                ),
                            )
                        },
                ),
        )
}
