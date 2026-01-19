package com.nextmall.product

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.product",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.product",
    "com.nextmall.common",
)
class ProductApplication

fun main(args: Array<String>) {
    runApplication<ProductApplication>(*args)
}
