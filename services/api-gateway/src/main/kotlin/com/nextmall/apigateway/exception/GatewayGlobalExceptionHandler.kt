package com.nextmall.apigateway.exception

import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.exception.code.CommonErrorCode
import com.nextmall.common.web.reactive.exception.GlobalExceptionHandlerSupport
import com.nextmall.common.web.core.exception.HttpStatusMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@RestControllerAdvice
class GatewayGlobalExceptionHandler(
    httpStatusMapper: HttpStatusMapper,
) : GlobalExceptionHandlerSupport(httpStatusMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    override fun handleUnexpectedException(
        ex: Exception,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        log.error("Unhandled exception occurred in API Gateway", ex)

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(
                    buildErrorResponse(
                        code = CommonErrorCode.INTERNAL_ERROR.code,
                        message = "Gateway error.",
                        path = exchange.request.path.value(),
                    ),
                ),
        )
    }
}
