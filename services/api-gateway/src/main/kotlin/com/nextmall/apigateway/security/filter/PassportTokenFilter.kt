package com.nextmall.apigateway.security.filter

import com.nextmall.apigateway.security.config.GatewaySecurityProperties
import com.nextmall.apigateway.security.token.GatewayPassportTokenIssuer
import com.nextmall.common.security.internal.SecurityTokenConstants
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono

@Component
class PassportTokenFilter(
    private val passportTokenIssuer: GatewayPassportTokenIssuer,
    securityProperties: GatewaySecurityProperties,
) : GlobalFilter,
    Ordered {
    private val permitAllPatterns: List<PathPattern> =
        securityProperties.permitAllPaths.map { PathPatternParser.defaultInstance.parse(it) }

    override fun filter(
        exchange: ServerWebExchange,
        chain: GatewayFilterChain,
    ): Mono<Void> =
        exchange
            .getPrincipal<JwtAuthenticationToken>()
            .map { it.token as Jwt }
            .map { externalToken -> passportTokenIssuer.issueFromExternalToken(externalToken) }
            .switchIfEmpty(
                Mono.defer {
                    if (isPermitAllPath(exchange)) {
                        Mono.fromSupplier { passportTokenIssuer.issueForGuest() }
                    } else {
                        Mono.empty()
                    }
                },
            ).flatMap { passportToken ->
                val mutatedRequest =
                    exchange.request
                        .mutate()
                        .header(
                            SecurityTokenConstants.PASSPORT_HEADER_NAME,
                            SecurityTokenConstants.BEARER_PREFIX + passportToken,
                        ).headers { it.remove("Authorization") }
                        .build()

                chain.filter(exchange.mutate().request(mutatedRequest).build()).thenReturn(true)
            }.switchIfEmpty(
                Mono.defer {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                    exchange.response.setComplete().thenReturn(false)
                },
            ).then()

    private fun isPermitAllPath(exchange: ServerWebExchange): Boolean {
        val path = exchange.request.path.pathWithinApplication()
        return permitAllPatterns.any { it.matches(path) }
    }

    // Must run AFTER Spring Security's filter chain (order -100)
    // to have access to the authenticated principal
    override fun getOrder(): Int = -50
}
