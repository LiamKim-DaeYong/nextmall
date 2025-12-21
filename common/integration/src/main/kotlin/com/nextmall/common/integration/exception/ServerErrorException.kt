package com.nextmall.common.integration.exception

class ServerErrorException(
    context: IntegrationErrorContext,
    cause: Throwable? = null,
) : IntegrationException(
        errorCode = IntegrationErrorCode.SERVER_ERROR,
        expected = false,
        cause = cause,
        context = context,
    )
