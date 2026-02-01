package com.nextmall.product.exception

import com.nextmall.common.web.mvc.exception.GlobalExceptionHandlerSupport
import com.nextmall.common.web.mvc.exception.HttpStatusMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ProductGlobalExceptionHandler(
    httpStatusMapper: HttpStatusMapper,
) : GlobalExceptionHandlerSupport(httpStatusMapper) {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun logUnexpected(ex: Exception) {
        log.error("Unexpected exception", ex)
    }
}
