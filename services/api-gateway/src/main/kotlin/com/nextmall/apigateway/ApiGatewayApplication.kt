package com.nextmall.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.boot.security.autoconfigure.ReactiveUserDetailsServiceAutoConfiguration

@SpringBootApplication(
    exclude = [ReactiveUserDetailsServiceAutoConfiguration::class],
    scanBasePackages = [
        "com.nextmall.apigateway",
        "com.nextmall.common",
    ],
)
@ConfigurationPropertiesScan(
    "com.nextmall.apigateway",
    "com.nextmall.common",
)
class ApiGatewayApplication

fun main(args: Array<String>) {
    runApplication<ApiGatewayApplication>(*args)
}
