package com.nextmall.bff.security

import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ServiceWebClientFactory(
    private val webClientFactory: WebClientFactory,
    private val tokenIssuer: BffServiceTokenIssuer,
) {
    fun create(
        baseUrl: String,
        targetService: String,
    ): WebClient =
        webClientFactory
            .create(baseUrl)
            .mutate()
            .filter(ServiceTokenFilter(tokenIssuer, targetService))
            .build()
}
