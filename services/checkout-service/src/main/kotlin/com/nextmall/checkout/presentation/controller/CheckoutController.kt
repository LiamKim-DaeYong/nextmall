package com.nextmall.checkout.presentation.controller

import com.nextmall.checkout.application.CheckoutService
import com.nextmall.checkout.domain.Checkout
import com.nextmall.checkout.domain.Order
import com.nextmall.checkout.presentation.request.CompleteCheckoutRequest
import com.nextmall.checkout.presentation.request.CreateCheckoutRequest
import com.nextmall.checkout.presentation.request.UpdateCheckoutRequest
import com.nextmall.checkout.presentation.request.toCommand
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/ucp/checkout")
class CheckoutController(
    private val checkoutService: CheckoutService,
) {
    @PostMapping
    fun createCheckout(
        @Valid @RequestBody request: CreateCheckoutRequest,
    ): ResponseEntity<Checkout> {
        val result = checkoutService.createCheckout(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result)
    }

    @GetMapping("/{checkoutId}")
    fun getCheckout(
        @PathVariable checkoutId: String,
    ): ResponseEntity<Checkout> {
        val result = checkoutService.getCheckout(checkoutId)
        return ResponseEntity
            .ok(result)
    }

    @PatchMapping("/{checkoutId}")
    fun updateCheckout(
        @PathVariable checkoutId: String,
        @Valid @RequestBody request: UpdateCheckoutRequest,
    ): ResponseEntity<Checkout> {
        val result = checkoutService.updateCheckout(checkoutId, request.toCommand())
        return ResponseEntity
            .ok(result)
    }

    @PostMapping("/{checkoutId}/complete")
    fun completeCheckout(
        @PathVariable checkoutId: String,
        @Valid @RequestBody request: CompleteCheckoutRequest,
    ): ResponseEntity<Order> {
        val result = checkoutService.completeCheckout(checkoutId, request.toCommand())
        return ResponseEntity
            .ok(result)
    }

    @PostMapping("/{checkoutId}/cancel")
    fun cancelCheckout(
        @PathVariable checkoutId: String,
    ): ResponseEntity<Checkout> {
        val result = checkoutService.cancelCheckout(checkoutId)
        return ResponseEntity
            .ok(result)
    }
}
