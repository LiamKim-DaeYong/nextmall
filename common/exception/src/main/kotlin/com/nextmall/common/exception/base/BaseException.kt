package com.nextmall.common.exception.base

import com.nextmall.common.exception.code.ErrorCode

abstract class BaseException(
    val errorCode: ErrorCode,
    val expected: Boolean = true,
    cause: Throwable? = null,
) : RuntimeException(errorCode.message, cause)
