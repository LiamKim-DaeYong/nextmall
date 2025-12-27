package com.nextmall.apigateway.routing

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRouteConfig(
    private val props: GatewayRoutesProperties,
) {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator =
        builder
            .routes()
            .route("bff-v1-route") {
                it
                    .path("/api/v1/**")
                    .filters { f -> f.stripPrefix(2) }
                    .uri(props.bff.baseUrl)
            }.build()
}
