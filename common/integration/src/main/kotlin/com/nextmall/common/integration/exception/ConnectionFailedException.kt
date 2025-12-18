package com.nextmall.common.integration.exception

import com.nextmall.common.exception.base.BaseException

class ConnectionFailedException(
    cause: Throwable? = null,
) : BaseException(
        errorCode = IntegrationErrorCode.CONNECTION_FAILED,
        expected = false,
        cause = cause,
    )
