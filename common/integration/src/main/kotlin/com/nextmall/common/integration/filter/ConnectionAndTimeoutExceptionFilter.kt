package com.nextmall.common.integration.filter

import com.nextmall.common.integration.exception.ConnectionFailedException
import com.nextmall.common.integration.exception.IntegrationErrorContext
import com.nextmall.common.integration.exception.IntegrationException
import com.nextmall.common.integration.exception.TimeoutException
import io.netty.handler.timeout.ReadTimeoutException
import io.netty.handler.timeout.WriteTimeoutException
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import java.net.ConnectException
import java.net.NoRouteToHostException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException as JdkTimeoutException

object ConnectionAndTimeoutExceptionFilter {
    fun filter(): ExchangeFilterFunction =
        ExchangeFilterFunction { request, next ->
            next
                .exchange(request)
                .onErrorMap { throwable ->
                    throwable as? IntegrationException
                        ?: mapToIntegrationException(request.url().toString(), throwable)
                }
        }

    private fun mapToIntegrationException(
        url: String,
        throwable: Throwable,
    ): Throwable =
        when (throwable) {
            is JdkTimeoutException,
            is ReadTimeoutException,
            is WriteTimeoutException,
            ->
                TimeoutException(
                    context =
                        IntegrationErrorContext(
                            url = url,
                            statusCode = null,
                            responseBody = null,
                        ),
                    cause = throwable,
                )

            is ConnectException,
            is UnknownHostException,
            is NoRouteToHostException,
            ->
                ConnectionFailedException(
                    context =
                        IntegrationErrorContext(
                            url = url,
                            statusCode = null,
                            responseBody = null,
                        ),
                    cause = throwable,
                )

            else -> throwable
        }
}
