package com.nextmall.apigateway.security.filter

import com.nextmall.apigateway.security.GatewaySecurityProperties
import com.nextmall.apigateway.security.support.JwtPresenceAuthenticationConverter
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.OrServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtPresenceAuthenticationFilter(
    converter: JwtPresenceAuthenticationConverter,
    properties: GatewaySecurityProperties,
) : AuthenticationWebFilter(
        ReactiveAuthenticationManager { authentication ->
            // Gateway does NOT validate token
            Mono.just(authentication)
        },
    ) {
    private val log = LoggerFactory.getLogger(javaClass)

    init {
        setServerAuthenticationConverter(converter)
        setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())

        // permitAll 경로는 필터 실행 제외
        val permitAllMatchers =
            properties.permitAllPaths
                .map { path ->
                    ServerWebExchangeMatchers.pathMatchers(path)
                }.toMutableList()

        // OPTIONS 요청도 제외
        permitAllMatchers.add(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**"))

        log.info("JWT Filter initialized with permitAll paths: {}", properties.permitAllPaths)

        setRequiresAuthenticationMatcher(
            NegatedServerWebExchangeMatcher(
                OrServerWebExchangeMatcher(permitAllMatchers),
            ),
        )

        setAuthenticationFailureHandler { webFilterExchange, exception ->
            log.warn(
                "JWT authentication failed: path={}, reason={}",
                webFilterExchange.exchange.request.path,
                exception.message,
            )
            val response = webFilterExchange.exchange.response
            response.statusCode = HttpStatus.UNAUTHORIZED
            response.setComplete()
        }
    }
}
