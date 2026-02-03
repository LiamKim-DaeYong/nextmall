package com.nextmall.common.web.reactive.client

import com.nextmall.common.web.reactive.client.filter.ConnectionAndTimeoutExceptionFilter
import com.nextmall.common.web.reactive.client.filter.HttpStatusExceptionFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import tools.jackson.databind.ObjectMapper

@Configuration
class WebClientConfig {
    @Bean
    fun webClientBuilder(objectMapper: ObjectMapper): WebClient.Builder =
        WebClient
            .builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .filter(HttpStatusExceptionFilter.filter(objectMapper))
            .filter(ConnectionAndTimeoutExceptionFilter.filter())

    @Bean
    fun webClientFactory(
        builder: WebClient.Builder,
    ): WebClientFactory = WebClientFactory(builder)
}
