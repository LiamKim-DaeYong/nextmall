package com.nextmall.bff.exception

import com.nextmall.common.web.reactive.exception.GlobalExceptionHandlerSupport
import com.nextmall.common.web.core.exception.HttpStatusMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class BffGlobalExceptionHandler(
    httpStatusMapper: HttpStatusMapper,
) : GlobalExceptionHandlerSupport(httpStatusMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun logUnexpected(ex: Exception) {
        log.error("Unexpected exception", ex)
    }
}
