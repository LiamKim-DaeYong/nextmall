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
        requestFactory.setConnectTimeout(properties.connectTimeout.toMillis().toIntSafely())
        requestFactory.setReadTimeout(properties.readTimeout.toMillis().toIntSafely())

        return RestClient
            .builder()
            .requestFactory(requestFactory)
    }

    private fun Long.toIntSafely(): Int =
        when {
            this <= 0L -> 0
            this > Int.MAX_VALUE.toLong() -> Int.MAX_VALUE
            else -> this.toInt()
        }
}
