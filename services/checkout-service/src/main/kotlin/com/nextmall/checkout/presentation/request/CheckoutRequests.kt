package com.nextmall.checkout.presentation.request

import com.nextmall.checkout.application.command.CompleteCheckoutCommand
import com.nextmall.checkout.application.command.CreateCheckoutCommand
import com.nextmall.checkout.application.command.UpdateCheckoutCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

data class CreateCheckoutRequest(
    @field:NotEmpty
    val lineItems: List<LineItemRequest>,
    @field:NotBlank
    val currency: String,
    val buyer: BuyerRequest? = null,
    val returnUrl: String? = null,
    val cancelUrl: String? = null,
)

data class UpdateCheckoutRequest(
    val lineItems: List<LineItemRequest>? = null,
    val buyer: BuyerRequest? = null,
    val shippingAddress: AddressRequest? = null,
    val billingAddress: AddressRequest? = null,
)

data class CompleteCheckoutRequest(
    @field:NotNull
    val payment: PaymentRequest,
    val confirm: Boolean? = null,
)

fun CreateCheckoutRequest.toCommand(): CreateCheckoutCommand =
    CreateCheckoutCommand(
        lineItems = lineItems.map { it.toCommand() },
        currency = currency,
        buyer = buyer?.toCommand(),
        returnUrl = returnUrl,
        cancelUrl = cancelUrl,
    )

fun UpdateCheckoutRequest.toCommand(): UpdateCheckoutCommand =
    UpdateCheckoutCommand(
        lineItems = lineItems?.map { it.toCommand() },
        buyer = buyer?.toCommand(),
        shippingAddress = shippingAddress?.toCommand(),
        billingAddress = billingAddress?.toCommand(),
    )

fun CompleteCheckoutRequest.toCommand(): CompleteCheckoutCommand =
    CompleteCheckoutCommand(
        payment = payment.toCommand(),
        confirm = confirm,
    )
