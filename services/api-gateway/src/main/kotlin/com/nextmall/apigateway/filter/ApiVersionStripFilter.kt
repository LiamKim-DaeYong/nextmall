package com.nextmall.apigateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class ApiVersionStripFilter :
    GlobalFilter,
    Ordered {
    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain,
    ): Mono<Void> {
        val path = exchange.request.uri.rawPath

        if (!path.startsWith("/api/v1/")) {
            return chain.filter(exchange)
        }

        val rewrittenPath = path.removePrefix("/api/v1")

        return chain.filter(
            exchange
                .mutate()
                .request { builder ->
                    builder.path(rewrittenPath)
                }.build(),
        )
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
}
