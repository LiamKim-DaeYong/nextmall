package com.nextmall.orchestrator.presentation.controller

import com.nextmall.orchestrator.application.order.command.CreateOrderCommand
import com.nextmall.orchestrator.application.order.command.CreateOrderFacade
import com.nextmall.orchestrator.presentation.request.order.CreateOrderOrchestrationRequest
import com.nextmall.orchestrator.presentation.response.order.CreateOrderOrchestrationResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/orchestrations/orders")
class OrderOrchestrationController(
    private val createOrderFacade: CreateOrderFacade,
) {
    @PostMapping
    fun createOrder(
        @Valid @RequestBody request: CreateOrderOrchestrationRequest,
    ): Mono<ResponseEntity<CreateOrderOrchestrationResponse>> {
        val command =
            CreateOrderCommand(
                userId = request.userId,
                productId = request.productId,
                quantity = request.quantity,
            )
        return createOrderFacade
            .createOrder(command)
            .map { result ->
                ResponseEntity.ok(CreateOrderOrchestrationResponse(result.orderId))
            }
    }
}
