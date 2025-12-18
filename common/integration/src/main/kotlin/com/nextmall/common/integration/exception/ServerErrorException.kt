package com.nextmall.common.integration.exception

import com.nextmall.common.exception.base.BaseException

class ServerErrorException(
    cause: Throwable? = null,
) : BaseException(
        errorCode = IntegrationErrorCode.SERVER_ERROR,
        expected = false,
        cause = cause,
    )
