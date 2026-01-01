package com.nextmall.bff.security

import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class AuthTokenRelayFilter(
    private val tokenProvider: AuthTokenProvider,
) : ExchangeFilterFunction {

    override fun filter(
        request: ClientRequest,
        next: ExchangeFunction,
    ): Mono<ClientResponse> {

        val token = tokenProvider.currentToken()

        if (token.isNullOrBlank()) {
            return next.exchange(request)
        }

        val newRequest =
            ClientRequest
                .from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .build()

        return next.exchange(newRequest)
    }
}
