package com.nextmall.common.web.mvc.client

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Scope
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.web.client.RestClient

@Configuration
@EnableConfigurationProperties(RestClientProperties::class)
class RestClientConfig {
    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    fun restClientBuilder(
        properties: RestClientProperties,
    ): RestClient.Builder {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(properties.connectTimeout.toMillis().toInt())
        requestFactory.setReadTimeout(properties.readTimeout.toMillis().toInt())

        return RestClient
            .builder()
            .requestFactory(requestFactory)
    }
}
