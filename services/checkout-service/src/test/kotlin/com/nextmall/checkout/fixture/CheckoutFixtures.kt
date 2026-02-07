package com.nextmall.checkout.fixture

import com.nextmall.checkout.application.command.CreateCheckoutCommand
import com.nextmall.checkout.application.command.LineItemCommand
import com.nextmall.checkout.application.command.MoneyCommand
import com.nextmall.checkout.application.command.UpdateCheckoutCommand

fun createCheckoutCommand(
    lineItems: List<LineItemCommand> = listOf(lineItemCommand()),
    currency: String = "KRW",
) = CreateCheckoutCommand(
    lineItems = lineItems,
    currency = currency,
    buyer = null,
    returnUrl = null,
    cancelUrl = null,
)

fun updateCheckoutCommand(
    lineItems: List<LineItemCommand>? = null,
) = UpdateCheckoutCommand(
    lineItems = lineItems,
    buyer = null,
    shippingAddress = null,
    billingAddress = null,
)

fun lineItemCommand(
    id: String = "line-1",
    title: String = "테스트 상품",
    quantity: Int = 1,
    amount: Long = 1000,
    currency: String = "KRW",
) = LineItemCommand(
    id = id,
    title = title,
    quantity = quantity,
    price = MoneyCommand(amount = amount, currency = currency),
    imageUrl = null,
)
