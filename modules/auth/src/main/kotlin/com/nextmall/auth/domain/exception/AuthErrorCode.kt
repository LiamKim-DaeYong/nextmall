package com.nextmall.auth.domain.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    INVALID_CREDENTIAL(
        code = "AUTH_401",
        message = "Invalid email or password.",
        category = ErrorCategory.AUTH,
    ),

    INVALID_REFRESH_TOKEN(
        code = "AUTH_401",
        message = "Invalid or expired refresh token.",
        category = ErrorCategory.AUTH,
    ),

    TOO_MANY_LOGIN_ATTEMPTS(
        code = "AUTH_429",
        message = "Too many login attempts. Please try again later.",
        category = ErrorCategory.AUTH,
    ),

    UNSUPPORTED_PROVIDER(
        code = "AUTH_500",
        message = "Authentication provider is not supported.",
        category = ErrorCategory.SYSTEM,
    ),

    INVALID_JWT_CONFIG(
        code = "AUTH_500",
        message = "Authentication system configuration error.",
        category = ErrorCategory.SYSTEM,
    ),
}
