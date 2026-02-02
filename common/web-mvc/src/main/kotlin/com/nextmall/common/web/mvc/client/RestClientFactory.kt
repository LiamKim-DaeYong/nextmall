package com.nextmall.common.web.mvc.client

import org.springframework.beans.factory.ObjectProvider
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class RestClientFactory(
    private val restClientBuilderProvider: ObjectProvider<RestClient.Builder>,
) {
    fun create(
        baseUrl: String,
        interceptor: ClientHttpRequestInterceptor? = null,
    ): RestClient {
        val builder = restClientBuilderProvider.getObject().baseUrl(baseUrl)
        if (interceptor != null) {
            builder.requestInterceptor(interceptor)
        }
        return builder.build()
    }
}
