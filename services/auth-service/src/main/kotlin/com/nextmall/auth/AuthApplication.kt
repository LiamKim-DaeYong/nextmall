package com.nextmall.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.auth",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.auth",
    "com.nextmall.common",
)
class AuthApplication

fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}
