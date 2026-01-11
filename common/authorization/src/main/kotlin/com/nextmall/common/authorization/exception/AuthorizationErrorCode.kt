package com.nextmall.common.authorization.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class AuthorizationErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    ACCESS_DENIED(
        code = "AUTHZ_ACCESS_DENIED",
        message = "Access denied",
        category = ErrorCategory.AUTH,
    ),

    POLICY_NOT_FOUND(
        code = "AUTHZ_POLICY_NOT_FOUND",
        message = "Policy not found for the requested resource and action",
        category = ErrorCategory.AUTH,
    ),

    INVALID_CONTEXT(
        code = "AUTHZ_INVALID_CONTEXT",
        message = "Invalid authorization context",
        category = ErrorCategory.VALIDATION,
    ),
}
