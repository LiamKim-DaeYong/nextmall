package com.nextmall.order.domain.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class OrderErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    ORDER_NOT_FOUND(
        code = "ORDER_NOT_FOUND",
        message = "Order not found",
        category = ErrorCategory.BUSINESS,
    ),
}
