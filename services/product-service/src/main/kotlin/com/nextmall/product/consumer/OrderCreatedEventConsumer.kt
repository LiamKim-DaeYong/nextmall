package com.nextmall.product.consumer

import com.nextmall.common.kafka.event.order.OrderCreatedEvent
import com.nextmall.product.application.ProductService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

@Component
class OrderCreatedEventConsumer(
    private val objectMapper: ObjectMapper,
    private val productService: ProductService,
) {
    @KafkaListener(
        topics = [ORDER_CREATED_TOPIC],
        containerFactory = "kafkaListenerContainerFactory",
    )
    fun handle(
        message: String,
        acknowledgment: Acknowledgment,
    ) {
        val event = objectMapper.readValue(message, OrderCreatedEvent::class.java)
        productService.decreaseStock(event.productId, event.quantity)
        acknowledgment.acknowledge()
    }

    companion object {
        private const val ORDER_CREATED_TOPIC = "order.created"
    }
}
