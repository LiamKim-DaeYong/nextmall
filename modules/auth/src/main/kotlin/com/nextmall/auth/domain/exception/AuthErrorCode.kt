package com.nextmall.auth.domain.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class AuthErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {

    INVALID_CREDENTIAL(
        code = "AUTH_INVALID_CREDENTIAL",
        message = "Invalid email or password.",
        category = ErrorCategory.AUTH,
    ),

    INVALID_REFRESH_TOKEN(
        code = "AUTH_INVALID_REFRESH_TOKEN",
        message = "Invalid or expired refresh token.",
        category = ErrorCategory.AUTH,
    ),

    DUPLICATE_ACCOUNT(
        code = "AUTH_DUPLICATE_ACCOUNT",
        message = "Account already exists.",
        category = ErrorCategory.BUSINESS,
    ),

    TOO_MANY_LOGIN_ATTEMPTS(
        code = "AUTH_TOO_MANY_LOGIN_ATTEMPTS",
        message = "Too many login attempts. Please try again later.",
        category = ErrorCategory.AUTH,
    ),

    UNSUPPORTED_PROVIDER(
        code = "AUTH_UNSUPPORTED_PROVIDER",
        message = "Authentication provider is not supported.",
        category = ErrorCategory.SYSTEM,
    ),

    INVALID_JWT_CONFIG(
        code = "AUTH_INVALID_JWT_CONFIG",
        message = "Authentication system configuration error.",
        category = ErrorCategory.SYSTEM,
    ),
}
