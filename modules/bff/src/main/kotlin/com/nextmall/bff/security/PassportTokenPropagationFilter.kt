package com.nextmall.bff.security

import com.nextmall.common.security.internal.PassportTokenConstants
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class PassportTokenPropagationFilter : ExchangeFilterFunction {
    override fun filter(
        request: ClientRequest,
        next: ExchangeFunction,
    ): Mono<ClientResponse> =
        ReactiveSecurityContextHolder
            .getContext()
            .map { it.authentication as JwtAuthenticationToken }
            .map { it.token.tokenValue }
            .map { token ->
                ClientRequest
                    .from(request)
                    .header(PassportTokenConstants.HEADER_NAME, "Bearer $token")
                    .build()
            }.flatMap { next.exchange(it) }
            .switchIfEmpty(next.exchange(request))
}
