package com.nextmall.bff.presentation.controller

import com.nextmall.bff.application.order.command.CreateOrderCommand
import com.nextmall.bff.application.order.command.CreateOrderFacade
import com.nextmall.bff.application.order.query.GetOrderFacade
import com.nextmall.bff.presentation.request.order.CreateOrderRequest
import com.nextmall.bff.presentation.response.order.CreateOrderResponse
import com.nextmall.bff.presentation.response.order.OrderViewResponse
import com.nextmall.bff.presentation.response.order.toResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orders")
class OrderController(
    private val createOrderFacade: CreateOrderFacade,
    private val getOrderFacade: GetOrderFacade,
) {
    @PostMapping
    fun createOrder(
        @Valid @RequestBody request: CreateOrderRequest,
    ): Mono<ResponseEntity<CreateOrderResponse>> {
        val command =
            CreateOrderCommand(
                userId = request.userId,
                productId = request.productId,
                quantity = request.quantity,
            )
        return createOrderFacade
            .createOrder(command)
            .map { result -> ResponseEntity.ok(CreateOrderResponse(result.orderId)) }
    }

    @GetMapping("/{orderId}")
    fun getOrder(
        @PathVariable orderId: Long,
    ): Mono<ResponseEntity<OrderViewResponse>> =
        getOrderFacade
            .getOrder(orderId)
            .map { result -> ResponseEntity.ok(result.toResponse()) }

    @GetMapping("/users/{userId}")
    fun getOrdersByUserId(
        @PathVariable userId: Long,
    ): Mono<ResponseEntity<List<OrderViewResponse>>> =
        getOrderFacade
            .getOrdersByUserId(userId)
            .map { results -> ResponseEntity.ok(results.map { it.toResponse() }) }
}
