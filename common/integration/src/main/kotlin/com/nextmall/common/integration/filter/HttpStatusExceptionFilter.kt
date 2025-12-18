package com.nextmall.common.integration.filter

import com.nextmall.common.integration.exception.ClientErrorException
import com.nextmall.common.integration.exception.IntegrationErrorContext
import com.nextmall.common.integration.exception.ServerErrorException
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

object HttpStatusExceptionFilter {
    fun filter(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofResponseProcessor { response: ClientResponse ->
            when {
                response.statusCode().is4xxClientError ->
                    response
                        .bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .flatMap { body ->
                            Mono.error(
                                ClientErrorException(
                                    context =
                                        IntegrationErrorContext(
                                            url = response.request().uri.toString(),
                                            statusCode = response.statusCode().value(),
                                            responseBody = body,
                                        ),
                                ),
                            )
                        }

                response.statusCode().is5xxServerError ->
                    response
                        .bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .flatMap { body ->
                            Mono.error(
                                ServerErrorException(
                                    context =
                                        IntegrationErrorContext(
                                            url = response.request().uri.toString(),
                                            statusCode = response.statusCode().value(),
                                            responseBody = body,
                                        ),
                                ),
                            )
                        }

                else ->
                    Mono.just(response)
            }
        }
}
