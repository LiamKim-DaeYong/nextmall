package com.nextmall.common.web.mvc.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode
import com.nextmall.common.exception.code.StatusCodeAware
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component

@Component
class HttpStatusMapper {
    fun map(errorCode: ErrorCode): HttpStatus {
        if (errorCode is StatusCodeAware) {
            return HttpStatus.valueOf(errorCode.statusCode)
        }

        return when (errorCode.category) {
            ErrorCategory.VALIDATION -> HttpStatus.BAD_REQUEST
            ErrorCategory.AUTH -> HttpStatus.UNAUTHORIZED
            ErrorCategory.BUSINESS -> HttpStatus.CONFLICT
            ErrorCategory.SYSTEM -> HttpStatus.INTERNAL_SERVER_ERROR
        }
    }
}
