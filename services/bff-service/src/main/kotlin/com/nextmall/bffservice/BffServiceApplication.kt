package com.nextmall.bffservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.bffservice",
        "com.nextmall.bff",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.bffservice",
    "com.nextmall.bff",
    "com.nextmall.common",
)
class BffServiceApplication

fun main(args: Array<String>) {
    runApplication<BffServiceApplication>(*args)
}
