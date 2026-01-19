package com.nextmall.auth.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class HttpStatusMapper {
    fun map(errorCode: ErrorCode): HttpStatus {
        if (errorCode is HttpStatusAware) {
            return errorCode.httpStatus
        }

        return when (errorCode.category) {
            ErrorCategory.VALIDATION -> HttpStatus.BAD_REQUEST
            ErrorCategory.AUTH -> HttpStatus.UNAUTHORIZED
            ErrorCategory.BUSINESS -> HttpStatus.CONFLICT
            ErrorCategory.SYSTEM -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
