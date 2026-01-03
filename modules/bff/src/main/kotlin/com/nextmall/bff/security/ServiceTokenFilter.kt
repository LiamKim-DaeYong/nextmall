package com.nextmall.bff.security

import com.nextmall.common.security.internal.ServiceTokenConstants
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono

class ServiceTokenFilter(
    private val tokenIssuer: BffServiceTokenIssuer,
    private val targetService: String,
) : ExchangeFilterFunction {
    override fun filter(
        request: ClientRequest,
        next: ExchangeFunction,
    ): Mono<ClientResponse> {
        val token = tokenIssuer.issueServiceToken(targetService)

        val newRequest =
            ClientRequest
                .from(request)
                .header(ServiceTokenConstants.TOKEN_HEADER, "$BEARER_PREFIX$token")
                .build()

        return next.exchange(newRequest)
    }

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }
}
