package com.nextmall.apigateway.exception

import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.exception.code.CommonErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GatewayGlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    fun handleGatewayException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception occurred in API Gateway", ex)

        val errorCode = CommonErrorCode.INTERNAL_ERROR

        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(
                ErrorResponse(
                    code = errorCode.code,
                    message = "Gateway error.",
                ),
            )
    }
}
