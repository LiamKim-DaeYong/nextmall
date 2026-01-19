package com.nextmall.product.domain.exception

import com.nextmall.common.exception.code.ErrorCategory
import com.nextmall.common.exception.code.ErrorCode

enum class ProductErrorCode(
    override val code: String,
    override val message: String,
    override val category: ErrorCategory,
) : ErrorCode {
    PRODUCT_NOT_FOUND(
        code = "PRODUCT_NOT_FOUND",
        message = "Product not found",
        category = ErrorCategory.BUSINESS,
    ),
}
