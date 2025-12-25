package com.nextmall.apigateway.route

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRouteConfig {

    @Bean
    fun route(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route("test-bff") {
                it.path("/api/v1/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri("http://localhost:8082")
            }
            .build()
}
