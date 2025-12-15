package com.nextmall.common.integration.support

import org.springframework.web.reactive.function.client.WebClient

class WebClientFactory(
    private val builder: WebClient.Builder,
) {
    fun create(baseUrl: String): WebClient =
        builder
            .clone()
            .baseUrl(baseUrl)
            .build()
}
