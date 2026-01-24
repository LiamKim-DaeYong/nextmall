package com.nextmall.orchestrator.security

import com.nextmall.common.security.internal.SecurityTokenConstants
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

/**
 * Orchestrator에서 다운스트림 서비스로 Passport Token을 전파하는 필터
 */
class PassportTokenPropagationFilter : ExchangeFilterFunction {
    override fun filter(request: ClientRequest, next: ExchangeFunction): Mono<ClientResponse> =
        ReactiveSecurityContextHolder
            .getContext()
            .mapNotNull { securityContext ->
                val authentication = securityContext.authentication
                if (authentication is JwtAuthenticationToken) {
                    authentication.token.tokenValue
                } else {
                    null
                }
            }.defaultIfEmpty("")
            .flatMap { token ->
                if (token.isNotEmpty()) {
                    val newRequest =
                        ClientRequest
                            .from(request)
                            .header(
                                SecurityTokenConstants.PASSPORT_HEADER_NAME,
                                SecurityTokenConstants.BEARER_PREFIX + token,
                            ).build()
                    next.exchange(newRequest)
                } else {
                    next.exchange(request)
                }
            }
}
