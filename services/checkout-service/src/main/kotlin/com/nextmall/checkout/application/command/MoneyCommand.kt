package com.nextmall.checkout.application.command

data class MoneyCommand(
    val amount: Long,
    val currency: String,
)
