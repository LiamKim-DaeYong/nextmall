package com.nextmall.common.integration.exception

import com.nextmall.common.exception.base.BaseException

class ClientErrorException(
    cause: Throwable? = null,
) : BaseException(
        errorCode = IntegrationErrorCode.CLIENT_ERROR,
        expected = false,
        cause = cause,
    )
