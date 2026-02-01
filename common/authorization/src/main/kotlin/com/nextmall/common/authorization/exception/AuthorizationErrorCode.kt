package com.nextmall.common.authorization.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode
import com.nextmall.common.exception.code.StatusCodeAware

enum class AuthorizationErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
    override val statusCode: Int,
) : ErrorCode,
    StatusCodeAware {
    ACCESS_DENIED(
        code = "AUTHZ_ACCESS_DENIED",
        message = "Access denied",
        category = ErrorCategory.AUTH,
        statusCode = 403,
    ),

    POLICY_NOT_FOUND(
        code = "AUTHZ_POLICY_NOT_FOUND",
        message = "Policy not found for the requested resource and action",
        category = ErrorCategory.AUTH,
        statusCode = 403,
    ),

    INVALID_CONTEXT(
        code = "AUTHZ_INVALID_CONTEXT",
        message = "Invalid authorization context",
        category = ErrorCategory.VALIDATION,
        statusCode = 400,
    ),
}
