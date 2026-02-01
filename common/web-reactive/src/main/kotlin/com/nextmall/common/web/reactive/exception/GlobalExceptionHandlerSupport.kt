package com.nextmall.common.web.reactive.exception

import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.exception.base.BaseException
import com.nextmall.common.exception.code.CommonErrorCode
import com.nextmall.common.integration.exception.IntegrationException
import jakarta.validation.ConstraintViolationException
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import reactor.core.publisher.Mono

open class GlobalExceptionHandlerSupport(
    private val httpStatusMapper: HttpStatusMapper,
) {
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(
        ex: WebExchangeBindException,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        val message =
            ex.bindingResult.fieldErrors
                .firstOrNull()
                ?.defaultMessage
                ?: CommonErrorCode.INVALID_REQUEST.message

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    buildErrorResponse(
                        code = CommonErrorCode.INVALID_REQUEST.code,
                        message = message,
                        path = exchange.request.path.value(),
                        details =
                            mapOf(
                                "fieldErrors" to
                                    ex.bindingResult.fieldErrors.map { error ->
                                        mapOf(
                                            "field" to error.field,
                                            "message" to
                                                (error.defaultMessage ?: CommonErrorCode.INVALID_REQUEST.message),
                                        )
                                    },
                            ),
                    ),
                ),
        )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        val message =
            ex.constraintViolations.firstOrNull()?.message
                ?: CommonErrorCode.INVALID_REQUEST.message

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    buildErrorResponse(
                        code = CommonErrorCode.INVALID_REQUEST.code,
                        message = message,
                        path = exchange.request.path.value(),
                        details =
                            mapOf(
                                "violations" to
                                    ex.constraintViolations.map { violation ->
                                        mapOf(
                                            "property" to violation.propertyPath.toString(),
                                            "message" to violation.message,
                                        )
                                    },
                            ),
                    ),
                ),
        )
    }

    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(
        ex: ServerWebInputException,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        val message = CommonErrorCode.INVALID_REQUEST.message

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                    buildErrorResponse(
                        code = CommonErrorCode.INVALID_REQUEST.code,
                        message = message,
                        path = exchange.request.path.value(),
                    ),
                ),
        )
    }

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        if (ex is IntegrationException) {
            val downstream = buildDownstreamResponse(ex, exchange)
            if (downstream != null) {
                return Mono.just(downstream)
            }
        }

        val status = httpStatusMapper.map(ex.errorCode)

        return Mono.just(
            ResponseEntity
                .status(status)
                .body(
                    buildErrorResponse(
                        code = ex.errorCode.code,
                        message = ex.errorCode.message,
                        path = exchange.request.path.value(),
                    ),
                ),
        )
    }

    @ExceptionHandler(Exception::class)
    open fun handleUnexpectedException(
        ex: Exception,
        exchange: ServerWebExchange,
    ): Mono<ResponseEntity<ErrorResponse>> {
        logUnexpected(ex)

        return Mono.just(
            ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                    buildErrorResponse(
                        code = CommonErrorCode.INTERNAL_ERROR.code,
                        message = CommonErrorCode.INTERNAL_ERROR.message,
                        path = exchange.request.path.value(),
                    ),
                ),
        )
    }

    protected open fun logUnexpected(ex: Exception) {
        // Override to add service-specific logging.
    }

    protected fun buildErrorResponse(
        code: String,
        message: String,
        path: String?,
        details: Map<String, Any?>? = null,
    ): ErrorResponse =
        ErrorResponse(
            code = code,
            message = message,
            path = path,
            traceId = resolveTraceId(),
            details = details,
        )

    protected open fun resolveTraceId(): String? =
        MDC.get("traceId") ?: MDC.get("trace_id")

    private fun buildDownstreamResponse(
        ex: IntegrationException,
        exchange: ServerWebExchange,
    ): ResponseEntity<ErrorResponse>? {
        val context = ex.context ?: return null
        val statusCode = context.statusCode ?: return null
        val errorResponse = context.errorResponse ?: return null

        return ResponseEntity
            .status(statusCode)
            .body(
                buildErrorResponse(
                    code = errorResponse.code,
                    message = errorResponse.message,
                    path = exchange.request.path.value(),
                    details =
                        mapOf(
                            "downstream" to
                                mapOf(
                                    "statusCode" to statusCode,
                                    "url" to context.url,
                                    "serviceName" to context.serviceName,
                                    "details" to errorResponse.details,
                                ),
                        ),
                ),
            )
    }
}
