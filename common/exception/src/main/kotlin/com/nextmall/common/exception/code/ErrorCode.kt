package com.nextmall.common.exception.code

interface ErrorCode {
    val code: String
    val message: String
    val category: ErrorCategory
}
