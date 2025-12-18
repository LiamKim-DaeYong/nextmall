package com.nextmall.common.integration.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class IntegrationErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    CONNECTION_FAILED(
        code = "INTEGRATION_CONNECTION_FAILED",
        message = "Failed to connect to downstream service",
        category = ErrorCategory.SYSTEM,
    ),

    TIMEOUT(
        code = "INTEGRATION_TIMEOUT",
        message = "Downstream service request timeout",
        category = ErrorCategory.SYSTEM,
    ),

    CLIENT_ERROR(
        code = "INTEGRATION_CLIENT_ERROR",
        message = "Downstream service client error",
        category = ErrorCategory.SYSTEM,
    ),

    SERVER_ERROR(
        code = "INTEGRATION_SERVER_ERROR",
        message = "Downstream service server error",
        category = ErrorCategory.SYSTEM,
    ),
}
