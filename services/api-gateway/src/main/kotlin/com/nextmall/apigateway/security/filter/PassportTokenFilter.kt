package com.nextmall.apigateway.security.filter

import com.nextmall.apigateway.security.token.GatewayPassportTokenIssuer
import com.nextmall.common.security.internal.PassportTokenConstants
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class PassportTokenFilter(
    private val passportTokenIssuer: GatewayPassportTokenIssuer,
) : GlobalFilter,
    Ordered {
    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain,
    ): Mono<Void> =
        exchange
            .getPrincipal<JwtAuthenticationToken>()
            .map { it.token as Jwt }
            .flatMap { externalToken ->
                val passportToken = passportTokenIssuer.issueFromExternalToken(externalToken)

                val mutatedRequest =
                    exchange.request
                        .mutate()
                        .header(PassportTokenConstants.HEADER_NAME, "Bearer $passportToken")
                        .headers { it.remove("Authorization") }
                        .build()

                chain.filter(exchange.mutate().request(mutatedRequest).build())
            }.switchIfEmpty(chain.filter(exchange))

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 1
}
