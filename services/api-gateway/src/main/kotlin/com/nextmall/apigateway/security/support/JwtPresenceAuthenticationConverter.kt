package com.nextmall.apigateway.security.support

import com.nextmall.apigateway.security.exception.InvalidJwtAuthenticationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtPresenceAuthenticationConverter : ServerAuthenticationConverter {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        log.debug("JWT Converter called for path: {}", exchange.request.path)

        val header =
            exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
                ?: return Mono
                    .error<Authentication>(InvalidJwtAuthenticationException("Missing Authorization header"))
                    .doOnError {
                        log.warn("JWT missing: path={}", exchange.request.path)
                    }

        if (!header.startsWith("Bearer ")) {
            return Mono
                .error<Authentication>(InvalidJwtAuthenticationException("Invalid Authorization header"))
                .doOnError {
                    log.warn("Invalid auth header format: path={}", exchange.request.path)
                }
        }

        val token = header.removePrefix("Bearer ").trim()
        if (token.isBlank()) {
            return Mono.error(InvalidJwtAuthenticationException("Empty token"))
        }

        log.debug("JWT present: path={}, tokenLength={}", exchange.request.path, token.length)
        return Mono.just(JwtPresenceAuthentication(token))
    }
}
