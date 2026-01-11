package com.nextmall.orderservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.orderservice",
        "com.nextmall.order",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.orderservice",
    "com.nextmall.order",
    "com.nextmall.common",
)
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}
