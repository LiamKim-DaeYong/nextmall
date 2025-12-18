package com.nextmall.common.integration.exception

import com.nextmall.common.exception.base.BaseException

class TimeoutException(
    cause: Throwable? = null,
) : BaseException(
        errorCode = IntegrationErrorCode.TIMEOUT,
        expected = false,
        cause = cause,
    )
