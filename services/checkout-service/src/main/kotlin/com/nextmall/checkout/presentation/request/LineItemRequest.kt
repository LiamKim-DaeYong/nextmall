package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.LineItemCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.Valid

data class LineItemRequest(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val title: String,
    @field:NotNull
    @field:Positive
    val quantity: Int,
    @field:NotNull
    @field:Valid
    val price: MoneyRequest,
    val imageUrl: String?,
)

fun LineItemRequest.toCommand(): LineItemCommand =
    LineItemCommand(
        id = id,
        title = title,
        quantity = quantity,
        price = price.toCommand(),
        imageUrl = imageUrl,
    )
