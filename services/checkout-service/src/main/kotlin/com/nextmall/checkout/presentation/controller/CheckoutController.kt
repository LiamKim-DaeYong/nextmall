package com.nextmall.checkout.presentation.controller

import com.nextmall.checkout.application.CheckoutService
import com.nextmall.checkout.presentation.response.CheckoutResponse
import com.nextmall.checkout.presentation.response.CheckoutSummaryResponse
import com.nextmall.checkout.presentation.response.OrderResponse
import com.nextmall.checkout.presentation.response.toResponse
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/checkouts")
class CheckoutController(
    private val checkoutService: CheckoutService,
) {
    @PostMapping
    fun createCheckout(
        @Valid @RequestBody request: CreateCheckoutRequest,
    ): ResponseEntity<CheckoutResponse> {
        val result = checkoutService.createCheckout(request.toCommand())
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result.toResponse())
    }

    @GetMapping("/{checkoutId}")
    fun getCheckout(
        @PathVariable checkoutId: String,
    ): ResponseEntity<CheckoutResponse> {
        val result = checkoutService.getCheckoutView(checkoutId)
        return ResponseEntity
            .ok(result.toResponse())
    }

    @GetMapping
    fun getCheckoutSummaries(
        @RequestParam(defaultValue = "20") limit: Int,
        @RequestParam(defaultValue = "0") offset: Int,
    ): ResponseEntity<List<CheckoutSummaryResponse>> {
        val results = checkoutService.getCheckoutSummaries(limit, offset)
        return ResponseEntity
            .ok(results.map { it.toResponse() })
    }

    @PatchMapping("/{checkoutId}")
    fun updateCheckout(
        @PathVariable checkoutId: String,
        @Valid @RequestBody request: UpdateCheckoutRequest,
    ): ResponseEntity<CheckoutResponse> {
        val result = checkoutService.updateCheckout(checkoutId, request.toCommand())
        return ResponseEntity
            .ok(result.toResponse())
    }

    @PostMapping("/{checkoutId}/complete")
    fun completeCheckout(
        @PathVariable checkoutId: String,
        @Valid @RequestBody request: CompleteCheckoutRequest,
    ): ResponseEntity<OrderResponse> {
        val result = checkoutService.completeCheckout(checkoutId, request.toCommand())
        return ResponseEntity
            .ok(result.toResponse())
    }

    @PostMapping("/{checkoutId}/cancel")
    fun cancelCheckout(
        @PathVariable checkoutId: String,
    ): ResponseEntity<CheckoutResponse> {
        val result = checkoutService.cancelCheckout(checkoutId)
        return ResponseEntity
            .ok(result.toResponse())
    }
}
