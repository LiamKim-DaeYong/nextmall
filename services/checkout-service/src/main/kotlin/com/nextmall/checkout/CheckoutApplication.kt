package com.nextmall.checkout

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.checkout",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.checkout",
    "com.nextmall.common",
)
class CheckoutApplication

fun main(args: Array<String>) {
    runApplication<CheckoutApplication>(*args)
}
