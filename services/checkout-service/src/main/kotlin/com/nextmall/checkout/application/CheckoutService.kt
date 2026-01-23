package com.nextmall.checkout.application

import com.nextmall.checkout.application.command.CompleteCheckoutCommand
import com.nextmall.checkout.application.command.CreateCheckoutCommand
import com.nextmall.checkout.application.command.LineItemCommand
import com.nextmall.checkout.application.command.UpdateCheckoutCommand
import com.nextmall.checkout.application.port.OrderPort
import com.nextmall.checkout.domain.Checkout
import com.nextmall.checkout.domain.Order
import com.nextmall.checkout.domain.exception.CheckoutNotFoundException
import com.nextmall.checkout.domain.model.CheckoutStatus
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.Money
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.PaymentHandler
import com.nextmall.checkout.domain.model.Totals
import com.nextmall.common.identifier.IdGenerator
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class CheckoutService(
    private val idGenerator: IdGenerator,
    private val orderPort: OrderPort,
) {
    private val checkouts = ConcurrentHashMap<String, Checkout>()

    fun createCheckout(command: CreateCheckoutCommand): Checkout {
        require(command.lineItems.isNotEmpty()) { "lineItems must not be empty" }
        require(command.currency.isNotBlank()) { "currency must not be blank" }

        val lineItems = command.lineItems.map { it.toDomain() }
        val totals = calculateTotals(lineItems, command.currency)

        val checkout =
            Checkout(
                id = "chk_${idGenerator.generate()}",
                status = CheckoutStatus.INCOMPLETE,
                currency = command.currency,
                lineItems = lineItems,
                totals = totals,
                messages = emptyList(),
                links =
                    Links(
                        terms = null,
                        privacy = null,
                        refund = null,
                    ),
                expiresAt = Instant.now().plusSeconds(6 * 60 * 60),
                payment =
                    Payment(
                        handlers =
                            listOf(
                                PaymentHandler(
                                    type = "card",
                                    provider = "internal",
                                ),
                            ),
                    ),
            )

        checkouts[checkout.id] = checkout
        return checkout
    }

    fun getCheckout(checkoutId: String): Checkout =
        checkouts[checkoutId]
            ?: throw CheckoutNotFoundException()

    fun updateCheckout(
        checkoutId: String,
        command: UpdateCheckoutCommand,
    ): Checkout {
        val existing =
            checkouts[checkoutId]
                ?: throw CheckoutNotFoundException()

        val lineItems =
            command.lineItems
                ?.map { it.toDomain() }
                ?: existing.lineItems

        val totals = calculateTotals(lineItems, existing.currency)

        val updated =
            existing.copy(
                lineItems = lineItems,
                totals = totals,
            )

        checkouts[checkoutId] = updated
        return updated
    }

    fun completeCheckout(
        checkoutId: String,
        command: CompleteCheckoutCommand,
    ): Order {
        val existing =
            checkouts[checkoutId]
                ?: throw CheckoutNotFoundException()

        val completed =
            existing.copy(
                status = CheckoutStatus.COMPLETED,
                payment = Payment(command.payment.handlers.map { PaymentHandler(it.type, it.provider) }),
            )

        checkouts[checkoutId] = completed

        return orderPort.createFromCheckout(completed)
    }

    fun cancelCheckout(checkoutId: String): Checkout {
        val existing =
            checkouts[checkoutId]
                ?: throw CheckoutNotFoundException()

        val canceled =
            existing.copy(
                status = CheckoutStatus.CANCELED,
            )

        checkouts[checkoutId] = canceled
        return canceled
    }

    private fun calculateTotals(
        lineItems: List<LineItem>,
        currency: String,
    ): Totals {
        val subtotalAmount =
            lineItems.sumOf { item ->
                item.price.amount * item.quantity
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
}
