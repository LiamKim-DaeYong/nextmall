package com.nextmall.common.integration.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.integration.exception.ClientErrorException
import com.nextmall.common.integration.exception.IntegrationErrorContext
import com.nextmall.common.integration.exception.ServerErrorException
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import reactor.core.publisher.Mono

object HttpStatusExceptionFilter {
    fun filter(objectMapper: ObjectMapper): ExchangeFilterFunction =
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
                                            errorResponse = parseErrorResponse(body, objectMapper),
                                            serviceName = resolveServiceName(response.request().uri.toString()),
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
                                            errorResponse = parseErrorResponse(body, objectMapper),
                                            serviceName = resolveServiceName(response.request().uri.toString()),
                                        ),
                                ),
                            )
                        }

                else ->
                    Mono.just(response)
            }
        }

    private fun parseErrorResponse(
        body: String,
        objectMapper: ObjectMapper,
    ): ErrorResponse? =
        if (body.isBlank()) {
            null
        } else {
            runCatching { objectMapper.readValue(body, ErrorResponse::class.java) }.getOrNull()
        }

    private fun resolveServiceName(url: String): String? =
        runCatching {
            java.net.URI(url).host
        }.getOrNull()
}
