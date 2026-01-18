package com.nextmall.user

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "com.nextmall.user",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.user",
    "com.nextmall.common",
)
class UserApplication

fun main(args: Array<String>) {
    runApplication<UserApplication>(*args)
}
