package com.nextmall.order.presentation.controller

import com.nextmall.order.application.OrderService
import com.nextmall.order.presentation.dto.CreateOrderSnapshotRequest
import com.nextmall.order.presentation.dto.OrderSnapshot
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
    fun getOrder(@PathVariable orderId: Long): ResponseEntity<OrderSnapshot> =
        ResponseEntity.ok(orderService.getOrder(orderId))

    @PostMapping
    fun createOrder(
        @Valid @RequestBody request: CreateOrderSnapshotRequest,
    ): ResponseEntity<OrderSnapshot> {
        // Phase 1: internal contract only. UCP mapping will live in orchestrator-service.
        val result = orderService.createOrder(request)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(result)
    }
}
