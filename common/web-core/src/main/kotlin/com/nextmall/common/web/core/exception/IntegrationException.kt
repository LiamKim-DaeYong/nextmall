package com.nextmall.common.web.core.exception

import com.nextmall.common.exception.base.BaseException

abstract class IntegrationException(
    errorCode: IntegrationErrorCode,
    expected: Boolean = false,
    cause: Throwable? = null,
    val context: IntegrationErrorContext? = null,
) : BaseException(
        errorCode = errorCode,
        expected = expected,
        cause = cause,
    )
