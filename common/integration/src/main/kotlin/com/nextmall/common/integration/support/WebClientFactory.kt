package com.nextmall.common.integration.support

import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

class WebClientFactory(
    private val builder: WebClient.Builder,
) {
    fun create(
        baseUrl: String,
        vararg filters: ExchangeFilterFunction,
    ): WebClient =
        builder
            .clone()
            .baseUrl(baseUrl)
            .filters { it.addAll(filters) }
            .build()
}
