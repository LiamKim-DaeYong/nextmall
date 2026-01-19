package com.nextmall.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.bff",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.bff",
    "com.nextmall.common",
)
class BffApplication

fun main(args: Array<String>) {
    runApplication<BffApplication>(*args)
}
