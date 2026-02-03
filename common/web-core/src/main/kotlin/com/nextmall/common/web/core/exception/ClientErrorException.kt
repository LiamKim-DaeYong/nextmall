package com.nextmall.common.web.core.exception

class ClientErrorException(
    context: IntegrationErrorContext,
    cause: Throwable? = null,
) : IntegrationException(
        errorCode = IntegrationErrorCode.CLIENT_ERROR,
        expected = false,
        cause = cause,
        context = context,
    )
