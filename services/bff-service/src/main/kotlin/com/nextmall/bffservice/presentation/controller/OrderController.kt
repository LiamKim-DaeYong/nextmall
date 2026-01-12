package com.nextmall.bffservice.presentation.controller

import com.nextmall.bff.application.order.command.CreateOrderCommand
import com.nextmall.bff.application.order.command.CreateOrderFacade
import com.nextmall.bff.application.order.query.GetOrderFacade
import com.nextmall.bffservice.presentation.request.order.CreateOrderRequest
import com.nextmall.bffservice.presentation.response.order.CreateOrderResponse
import com.nextmall.bffservice.presentation.response.order.OrderViewResponse
import com.nextmall.bffservice.presentation.response.order.toResponse
import jakarta.validation.Valid
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
    private val createOrderFacade: CreateOrderFacade,
    private val getOrderFacade: GetOrderFacade,
) {
    @PostMapping
    suspend fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest,
    ): ResponseEntity<CreateOrderResponse> {
        val command =
            CreateOrderCommand(
                userId = request.userId,
                productId = request.productId,
                quantity = request.quantity,
            )
        val result = createOrderFacade.createOrder(command)
        return ResponseEntity.ok(CreateOrderResponse(result.orderId))
    }

    @GetMapping("/{orderId}")
    suspend fun getOrder(
        @PathVariable orderId: Long,
    ): ResponseEntity<OrderViewResponse> {
        val result = getOrderFacade.getOrder(orderId)
        return ResponseEntity.ok(result.toResponse())
    }

    @GetMapping("/users/{userId}")
    suspend fun getOrdersByUserId(
        @PathVariable userId: Long,
    ): ResponseEntity<List<OrderViewResponse>> {
        val results = getOrderFacade.getOrdersByUserId(userId)
        return ResponseEntity.ok(results.map { it.toResponse() })
    }
}
