package com.nextmall.checkout.application.command

data class LineItemCommand(
    val id: String,
    val title: String,
    val quantity: Int,
    val price: MoneyCommand,
    val imageUrl: String?,
)
