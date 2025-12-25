package com.nextmall.user.application.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class UserErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    USER_NOT_FOUND(
        code = "USER_NOT_FOUND",
        message = "User not found",
        category = ErrorCategory.BUSINESS,
    ),
}
