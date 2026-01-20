package com.nextmall.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.order",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.order",
    "com.nextmall.common",
)
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
