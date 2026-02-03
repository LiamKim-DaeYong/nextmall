package com.nextmall.common.web.mvc.exception

import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.exception.base.BaseException
import com.nextmall.common.exception.code.CommonErrorCode
import com.nextmall.common.web.core.exception.IntegrationException
import com.nextmall.common.web.core.exception.HttpStatusMapper
import jakarta.validation.ConstraintViolationException
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import jakarta.servlet.http.HttpServletRequest

open class GlobalExceptionHandlerSupport(
    private val httpStatusMapper: HttpStatusMapper,
) {
    @ExceptionHandler(MethodArgumentNotValidException::class, BindException::class)
    fun handleValidationException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val message =
            when (ex) {
                is MethodArgumentNotValidException ->
                    ex.bindingResult.fieldErrors
                        .firstOrNull()
                        ?.defaultMessage
                is BindException ->
                    ex.bindingResult.fieldErrors
                        .firstOrNull()
                        ?.defaultMessage
                else -> null
            } ?: CommonErrorCode.INVALID_REQUEST.message

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                buildErrorResponse(
                    code = CommonErrorCode.INVALID_REQUEST.code,
                    message = message,
                    path = request.requestURI,
                    details =
                        when (ex) {
                            is MethodArgumentNotValidException ->
                                mapOf(
                                    "fieldErrors" to
                                        ex.bindingResult.fieldErrors.map { error ->
                                            mapOf(
                                                "field" to error.field,
                                                "message" to
                                                    (error.defaultMessage ?: CommonErrorCode.INVALID_REQUEST.message),
                                            )
                                        },
                                )
                            is BindException ->
                                mapOf(
                                    "fieldErrors" to
                                        ex.bindingResult.fieldErrors.map { error ->
                                            mapOf(
                                                "field" to error.field,
                                                "message" to
                                                    (error.defaultMessage ?: CommonErrorCode.INVALID_REQUEST.message),
                                            )
                                        },
                                )
                            else -> null
                        },
                ),
            )
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(
        ex: ConstraintViolationException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        val message =
            ex.constraintViolations.firstOrNull()?.message
                ?: CommonErrorCode.INVALID_REQUEST.message

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                buildErrorResponse(
                    code = CommonErrorCode.INVALID_REQUEST.code,
                    message = message,
                    path = request.requestURI,
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
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                buildErrorResponse(
                    code = CommonErrorCode.INVALID_REQUEST.code,
                    message = CommonErrorCode.INVALID_REQUEST.message,
                    path = request.requestURI,
                ),
            )

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(
        ex: BaseException,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        if (ex is IntegrationException) {
            val downstream = buildDownstreamResponse(ex, request)
            if (downstream != null) {
                return downstream
            }
        }

        val status = httpStatusMapper.map(ex.errorCode)

        return ResponseEntity
            .status(status)
            .body(
                buildErrorResponse(
                    code = ex.errorCode.code,
                    message = ex.errorCode.message,
                    path = request.requestURI,
                ),
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(
        ex: Exception,
        request: HttpServletRequest,
    ): ResponseEntity<ErrorResponse> {
        logUnexpected(ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                buildErrorResponse(
                    code = CommonErrorCode.INTERNAL_ERROR.code,
                    message = CommonErrorCode.INTERNAL_ERROR.message,
                    path = request.requestURI,
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
        request: HttpServletRequest,
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
                    path = request.requestURI,
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
