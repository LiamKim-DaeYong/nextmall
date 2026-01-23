package com.nextmall.checkout.application

import com.nextmall.checkout.application.command.CompleteCheckoutCommand
import com.nextmall.checkout.application.command.CreateCheckoutCommand
import com.nextmall.checkout.application.command.LineItemCommand
import com.nextmall.checkout.application.command.UpdateCheckoutCommand
import com.nextmall.checkout.application.query.CheckoutSummaryView
import com.nextmall.checkout.application.query.CheckoutView
import com.nextmall.checkout.domain.Checkout
import com.nextmall.checkout.domain.Order
import com.nextmall.checkout.domain.exception.CheckoutNotFoundException
import com.nextmall.checkout.domain.model.Adjustment
import com.nextmall.checkout.domain.model.Address
import com.nextmall.checkout.domain.model.Buyer
import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.checkout.domain.model.Fulfillment
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.Money
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.PaymentHandler
import com.nextmall.checkout.domain.model.Totals
import com.nextmall.checkout.infrastructure.persistence.jooq.CheckoutJooqRepository
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutEntity
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutJpaRepository
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutLineItemEntity
import com.nextmall.checkout.infrastructure.persistence.jpa.CheckoutPaymentHandlerEntity
import com.nextmall.common.identifier.IdGenerator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class CheckoutService(
    private val idGenerator: IdGenerator,
    private val checkoutJpaRepository: CheckoutJpaRepository,
    private val checkoutJooqRepository: CheckoutJooqRepository,
) {
    @Transactional
    fun createCheckout(command: CreateCheckoutCommand): Checkout {
        require(command.lineItems.isNotEmpty()) { "lineItems must not be empty" }
        require(command.currency.isNotBlank()) { "currency must not be blank" }

        val lineItems = command.lineItems.map { it.toDomain() }
        require(lineItems.all { it.price.currency == command.currency }) {
            "All line items must match checkout currency"
        }
        val totals = calculateTotals(lineItems, command.currency)

        val checkoutId = "chk_${idGenerator.generate()}"
        val expiresAt = Instant.now().plusSeconds(6 * 60 * 60)
        val paymentHandler = PaymentHandler(type = "card", provider = "internal")
        val buyer = command.buyer?.toDomain()
        val paymentHandlers = listOf(paymentHandler)

        val entity =
            CheckoutEntity(
                id = checkoutId,
                status = CheckoutStatus.INCOMPLETE,
                currency = command.currency,
                buyerId = buyer?.id,
                buyerEmail = buyer?.email,
                buyerName = buyer?.name,
                shippingLine1 = null,
                shippingLine2 = null,
                shippingCity = null,
                shippingRegion = null,
                shippingPostalCode = null,
                shippingCountry = null,
                billingLine1 = null,
                billingLine2 = null,
                billingCity = null,
                billingRegion = null,
                billingPostalCode = null,
                billingCountry = null,
                subtotalAmount = totals.subtotal.amount,
                taxAmount = totals.tax.amount,
                shippingAmount = totals.shipping.amount,
                discountAmount = totals.discount.amount,
                totalAmount = totals.total.amount,
                termsUrl = null,
                privacyUrl = null,
                refundUrl = null,
                expiresAt = expiresAt,
                paymentType = paymentHandler.type,
                paymentProvider = paymentHandler.provider,
                paymentHandlers = paymentHandlers.toEntities(),
                lineItems =
                    lineItems.map { it.toEntity() }
                        .toMutableList(),
            )

        return checkoutJpaRepository.save(entity).toDomain()
    }

    @Transactional(readOnly = true)
    fun getCheckout(checkoutId: String): Checkout =
        checkoutJpaRepository
            .findById(checkoutId)
            .orElseThrow { CheckoutNotFoundException() }
            .toDomain()

    @Transactional(readOnly = true)
    fun getCheckoutView(checkoutId: String): CheckoutView =
        checkoutJooqRepository.findById(checkoutId)
            ?: throw CheckoutNotFoundException()

    @Transactional(readOnly = true)
    fun getCheckoutSummaries(
        limit: Int,
        offset: Int,
    ): List<CheckoutSummaryView> =
        checkoutJooqRepository.findAllSummaries(limit, offset)

    @Transactional
    fun updateCheckout(
        checkoutId: String,
        command: UpdateCheckoutCommand,
    ): Checkout {
        val entity =
            checkoutJpaRepository
                .findById(checkoutId)
                .orElseThrow { CheckoutNotFoundException() }

        val lineItems =
            command.lineItems
                ?.map { it.toDomain() }
                ?: entity.toDomain().lineItems

        val totals = calculateTotals(lineItems, entity.currency)
        val buyer = command.buyer?.toDomain()
        val shippingAddress = command.shippingAddress?.toDomain()
        val billingAddress = command.billingAddress?.toDomain()

        entity.lineItems.clear()
        entity.lineItems.addAll(lineItems.map { it.toEntity() })
        entity.buyerId = buyer?.id ?: entity.buyerId
        entity.buyerEmail = buyer?.email ?: entity.buyerEmail
        entity.buyerName = buyer?.name ?: entity.buyerName
        entity.shippingLine1 = shippingAddress?.line1 ?: entity.shippingLine1
        entity.shippingLine2 = shippingAddress?.line2 ?: entity.shippingLine2
        entity.shippingCity = shippingAddress?.city ?: entity.shippingCity
        entity.shippingRegion = shippingAddress?.region ?: entity.shippingRegion
        entity.shippingPostalCode = shippingAddress?.postalCode ?: entity.shippingPostalCode
        entity.shippingCountry = shippingAddress?.country ?: entity.shippingCountry
        entity.billingLine1 = billingAddress?.line1 ?: entity.billingLine1
        entity.billingLine2 = billingAddress?.line2 ?: entity.billingLine2
        entity.billingCity = billingAddress?.city ?: entity.billingCity
        entity.billingRegion = billingAddress?.region ?: entity.billingRegion
        entity.billingPostalCode = billingAddress?.postalCode ?: entity.billingPostalCode
        entity.billingCountry = billingAddress?.country ?: entity.billingCountry
        entity.subtotalAmount = totals.subtotal.amount
        entity.taxAmount = totals.tax.amount
        entity.shippingAmount = totals.shipping.amount
        entity.discountAmount = totals.discount.amount
        entity.totalAmount = totals.total.amount

        return checkoutJpaRepository.save(entity).toDomain()
    }

    @Transactional
    fun completeCheckout(
        checkoutId: String,
        command: CompleteCheckoutCommand,
    ): Order {
        val entity =
            checkoutJpaRepository
                .findById(checkoutId)
                .orElseThrow { CheckoutNotFoundException() }

        val handler = command.payment.handlers.firstOrNull()
        entity.status = CheckoutStatus.COMPLETED
        entity.paymentType = handler?.type
        entity.paymentProvider = handler?.provider
        entity.paymentHandlers.clear()
        entity.paymentHandlers.addAll(
            command.payment.handlers
                .map { PaymentHandler(it.type, it.provider) }
                .toEntities(),
        )

        val completed = checkoutJpaRepository.save(entity).toDomain()

        return Order(
            id = "ord_${idGenerator.generate()}",
            checkoutId = completed.id,
            permalinkUrl = "/orders/${completed.id}",
            lineItems = completed.lineItems,
            fulfillment = Fulfillment(expectations = emptyList(), events = emptyList()),
            adjustments =
                listOf(
                    Adjustment(
                        type = "none",
                        amount = Money(amount = 0, currency = completed.currency),
                    ),
                ),
            totals = completed.totals,
        )
    }

    @Transactional
    fun cancelCheckout(checkoutId: String): Checkout {
        val entity =
            checkoutJpaRepository
                .findById(checkoutId)
                .orElseThrow { CheckoutNotFoundException() }

        entity.status = CheckoutStatus.CANCELED

        return checkoutJpaRepository.save(entity).toDomain()
    }

    private fun calculateTotals(
        lineItems: List<LineItem>,
        currency: String,
    ): Totals {
        val subtotalAmount =
            lineItems.fold(0L) { acc, item ->
                val lineTotal = Math.multiplyExact(item.price.amount, item.quantity.toLong())
                Math.addExact(acc, lineTotal)
            }

        val zero = Money(amount = 0, currency = currency)
        val subtotal = Money(amount = subtotalAmount, currency = currency)

        return Totals(
            subtotal = subtotal,
            tax = zero,
            shipping = zero,
            discount = zero,
            total = subtotal,
        )
    }

    private fun LineItemCommand.toDomain(): LineItem =
        LineItem(
            id = id,
            title = title,
            quantity = quantity,
            price = Money(amount = price.amount, currency = price.currency),
            imageUrl = imageUrl,
        )

    private fun LineItem.toEntity(): CheckoutLineItemEntity =
        CheckoutLineItemEntity(
            lineItemId = id,
            title = title,
            quantity = quantity,
            priceAmount = price.amount,
            priceCurrency = price.currency,
            imageUrl = imageUrl,
        )

    private fun List<PaymentHandler>.toEntities(): List<CheckoutPaymentHandlerEntity> =
        mapIndexed { index, handler ->
            CheckoutPaymentHandlerEntity(
                type = handler.type,
                provider = handler.provider,
                sortOrder = index,
            )
        }

    private fun com.nextmall.checkout.application.command.BuyerCommand.toDomain(): Buyer =
        Buyer(
            id = id,
            email = email,
            name = name,
        )

    private fun com.nextmall.checkout.application.command.AddressCommand.toDomain(): Address =
        Address(
            line1 = line1,
            line2 = line2,
            city = city,
            region = region,
            postalCode = postalCode,
            country = country,
        )
}
