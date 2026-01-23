package com.nextmall.checkout.presentation.response

import com.nextmall.checkout.domain.Checkout
import com.nextmall.checkout.domain.Order
import com.nextmall.checkout.application.query.CheckoutView
import com.nextmall.checkout.domain.model.Address
import com.nextmall.checkout.domain.model.LineItem
import com.nextmall.checkout.domain.model.Links
import com.nextmall.checkout.domain.model.Message
import com.nextmall.checkout.domain.model.Money
import com.nextmall.checkout.domain.model.Payment
import com.nextmall.checkout.domain.model.PaymentHandler
import com.nextmall.checkout.domain.model.Totals

fun Checkout.toResponse(): CheckoutResponse =
    CheckoutResponse(
        id = id,
        status = status.name.lowercase(),
        currency = currency,
        lineItems = lineItems.map { it.toResponse() },
        buyer = buyer?.let { BuyerResponse(id = it.id, email = it.email, name = it.name) },
        shippingAddress = shippingAddress?.toResponse(),
        billingAddress = billingAddress?.toResponse(),
        totals = totals.toResponse(),
        messages = messages.map { it.toResponse() },
        links = links.toResponse(),
        expiresAt = expiresAt?.toString(),
        payment = payment.toResponse(),
    )

fun Order.toResponse(): OrderResponse =
    OrderResponse(
        id = id,
        checkoutId = checkoutId,
        permalinkUrl = permalinkUrl,
        lineItems = lineItems.map { it.toResponse() },
        totals = totals.toResponse(),
    )

private fun LineItem.toResponse(): LineItemResponse =
    LineItemResponse(
        id = id,
        title = title,
        quantity = quantity,
        price = price.toResponse(),
        imageUrl = imageUrl,
    )

private fun Money.toResponse(): MoneyResponse =
    MoneyResponse(
        amount = amount,
        currency = currency,
    )

private fun Totals.toResponse(): TotalsResponse =
    TotalsResponse(
        subtotal = subtotal.toResponse(),
        tax = tax.toResponse(),
        shipping = shipping.toResponse(),
        discount = discount.toResponse(),
        total = total.toResponse(),
    )

private fun Links.toResponse(): LinksResponse =
    LinksResponse(
        terms = terms,
        privacy = privacy,
        refund = refund,
    )

private fun Message.toResponse(): MessageResponse =
    MessageResponse(
        code = code,
        message = message,
        severity = severity.name.lowercase(),
    )

private fun Payment.toResponse(): PaymentResponse =
    PaymentResponse(
        handlers = handlers.map { it.toResponse() },
    )

private fun PaymentHandler.toResponse(): PaymentHandlerResponse =
    PaymentHandlerResponse(
        type = type,
        provider = provider,
    )

private fun Address.toResponse(): AddressResponse =
    AddressResponse(
        line1 = line1,
        line2 = line2,
        city = city,
        region = region,
        postalCode = postalCode,
        country = country,
    )
fun CheckoutView.toResponse(): CheckoutResponse =
    CheckoutResponse(
        id = id,
        status = status.name.lowercase(),
        currency = currency,
        lineItems = lineItems.map { it.toResponse() },
        buyer = buyer?.let { BuyerResponse(id = it.id, email = it.email, name = it.name) },
        shippingAddress = shippingAddress?.toResponse(),
        billingAddress = billingAddress?.toResponse(),
        totals = totals.toResponse(),
        messages = messages.map { it.toResponse() },
        links = links.toResponse(),
        expiresAt = expiresAt?.toString(),
        payment = payment.toResponse(),
    )

fun com.nextmall.checkout.application.query.CheckoutSummaryView.toResponse(): CheckoutSummaryResponse =
    CheckoutSummaryResponse(
        id = id,
        status = status.name.lowercase(),
        currency = currency,
        totalAmount = totalAmount,
    )
