package com.nextmall.order.presentation.controller

import com.nextmall.common.security.principal.AuthenticatedPrincipal
import com.nextmall.common.security.spring.CurrentUser
import com.nextmall.order.application.OrderService
import com.nextmall.order.presentation.request.CreateOrderRequest
import com.nextmall.order.presentation.response.CreateOrderResponse
import com.nextmall.order.presentation.response.OrderViewResponse
import com.nextmall.order.presentation.response.toResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(
    private val orderService: OrderService,
) {
    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: Long): ResponseEntity<OrderViewResponse> {
        val result = orderService.getOrder(orderId)
        return ResponseEntity
            .ok(result.toResponse())
    }

    @GetMapping
    fun getMyOrders(
        @CurrentUser principal: AuthenticatedPrincipal,
    ): ResponseEntity<List<OrderViewResponse>> {
        val results = orderService.getOrdersByUserId(principal.userIdAsLong())
        return ResponseEntity
            .ok(results.map { it.toResponse() })
    }

    @PostMapping
    fun createOrder(
        @CurrentUser principal: AuthenticatedPrincipal,
        @Valid @RequestBody request: CreateOrderRequest,
    ): ResponseEntity<CreateOrderResponse> {
        val result =
            orderService.createOrder(
                userId = principal.userIdAsLong(),
                productId = request.productId,
                quantity = request.quantity,
            )
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(CreateOrderResponse(result.orderId))
    }

    @PostMapping("/{orderId}/cancel")
    fun cancelOrder(@PathVariable orderId: Long): ResponseEntity<Unit> {
        orderService.cancelOrder(orderId)
        return ResponseEntity
            .noContent()
            .build()
    }
}
