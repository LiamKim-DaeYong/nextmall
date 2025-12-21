package com.nextmall.common.integration.config

import com.nextmall.common.integration.filter.ConnectionAndTimeoutExceptionFilter
import com.nextmall.common.integration.filter.HttpStatusExceptionFilter
import com.nextmall.common.integration.support.WebClientFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientBaseConfig {
    @Bean
    fun webClientBuilder(): WebClient.Builder =
        WebClient
            .builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .filter(HttpStatusExceptionFilter.filter())
            .filter(ConnectionAndTimeoutExceptionFilter.filter())

    @Bean
    fun webClientFactory(
        builder: WebClient.Builder,
    ): WebClientFactory = WebClientFactory(builder)
}
