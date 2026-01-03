package com.nextmall.bff.security

import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class AuthenticatedWebClientFactory(
    private val webClientFactory: WebClientFactory,
    private val tokenProvider: AuthTokenProvider,
) {
    fun create(baseUrl: String): WebClient =
        webClientFactory
            .create(baseUrl)
            .mutate()
            .filter(AuthTokenRelayFilter(tokenProvider))
            .build()
}
