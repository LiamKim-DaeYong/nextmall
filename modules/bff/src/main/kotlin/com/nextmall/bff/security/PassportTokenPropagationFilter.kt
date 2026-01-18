package com.nextmall.bff.security

import com.nextmall.common.security.internal.PassportTokenConstants
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

/**
 * BFF에서 다운스트림 서비스로 Passport Token을 전파하는 필터
 *
 * Phase 2: Gateway가 이미 Passport Token을 발급했으므로
 * BFF는 SecurityContext에서 토큰을 가져와 그대로 전파
 */
class PassportTokenPropagationFilter : ExchangeFilterFunction {
    override fun filter(
        request: ClientRequest,
        next: ExchangeFunction,
    ): Mono<ClientResponse> {
        val authentication = SecurityContextHolder.getContext().authentication

        return if (authentication is JwtAuthenticationToken) {
            val token = authentication.token.tokenValue
            val newRequest =
                ClientRequest
                    .from(request)
                    .header(PassportTokenConstants.HEADER_NAME, "Bearer $token")
                    .build()
            next.exchange(newRequest)
        } else {
            next.exchange(request)
        }
    }
}
