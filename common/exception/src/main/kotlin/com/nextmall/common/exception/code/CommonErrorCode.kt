package com.nextmall.common.exception.code

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    INTERNAL_ERROR(
        code = "COMMON_INTERNAL_ERROR",
        message = "Internal server error",
        category = ErrorCategory.SYSTEM,
    ),

    INVALID_REQUEST(
        code = "COMMON_INVALID_REQUEST",
        message = "Invalid request",
        category = ErrorCategory.VALIDATION,
    ),
}
