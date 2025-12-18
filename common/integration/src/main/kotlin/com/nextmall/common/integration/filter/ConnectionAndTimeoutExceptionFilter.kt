package com.nextmall.common.integration.filter

import com.nextmall.common.integration.exception.ConnectionFailedException
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
                    mapToIntegrationException(throwable)
                }
        }

    private fun mapToIntegrationException(throwable: Throwable): Throwable =
        when (throwable) {
            is JdkTimeoutException,
            is ReadTimeoutException,
            is WriteTimeoutException,
            ->
                TimeoutException(cause = throwable)

            is ConnectException,
            is UnknownHostException,
            is NoRouteToHostException,
            ->
                ConnectionFailedException(cause = throwable)

            else -> throwable
        }
}
