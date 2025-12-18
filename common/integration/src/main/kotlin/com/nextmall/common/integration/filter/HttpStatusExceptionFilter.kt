package com.nextmall.common.integration.filter

import com.nextmall.common.integration.exception.ClientErrorException
import com.nextmall.common.integration.exception.ServerErrorException
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

object HttpStatusExceptionFilter {
    fun filter(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofResponseProcessor { response: ClientResponse ->
            when {
                response.statusCode().is4xxClientError ->
                    Mono.error(ClientErrorException())

                response.statusCode().is5xxServerError ->
                    Mono.error(ServerErrorException())

                else ->
                    Mono.just(response)
            }
        }
}
