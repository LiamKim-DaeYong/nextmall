package com.nextmall.common.exception.code

enum class CommonErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    INTERNAL_ERROR("COMMON_500", "Internal server error", ErrorCategory.SYSTEM),
    INVALID_REQUEST("COMMON_400", "Invalid request", ErrorCategory.VALIDATION),
}
