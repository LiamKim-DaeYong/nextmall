package com.nextmall.checkout.domain.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class CheckoutErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    CHECKOUT_NOT_FOUND(
        code = "CHECKOUT_NOT_FOUND",
        message = "Checkout not found",
        category = ErrorCategory.BUSINESS,
    ),
}
