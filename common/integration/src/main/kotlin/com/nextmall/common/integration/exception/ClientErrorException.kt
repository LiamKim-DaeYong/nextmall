package com.nextmall.common.integration.exception

class ClientErrorException(
    context: IntegrationErrorContext,
    cause: Throwable? = null,
) : IntegrationException(
        errorCode = IntegrationErrorCode.CLIENT_ERROR,
        expected = false,
        cause = cause,
        context = context,
    )
