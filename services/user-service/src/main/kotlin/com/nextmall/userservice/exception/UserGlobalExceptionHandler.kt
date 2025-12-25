package com.nextmall.userservice.exception

import com.nextmall.common.exception.ErrorResponse
import com.nextmall.common.exception.base.BaseException
import com.nextmall.common.exception.code.CommonErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class UserGlobalExceptionHandler(
    private val httpStatusMapper: HttpStatusMapper,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(BaseException::class)
    fun handleBaseException(ex: BaseException): ResponseEntity<ErrorResponse> {
        val status = httpStatusMapper.map(ex.errorCode)

        return ResponseEntity
            .status(status)
            .body(
                ErrorResponse(
                    code = ex.errorCode.code,
                    message = ex.errorCode.message,
                ),
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorCode = CommonErrorCode.INTERNAL_ERROR

        logger.error("Unexpected exception", ex)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    code = errorCode.code,
                    message = errorCode.message,
                ),
            )
    }
}
