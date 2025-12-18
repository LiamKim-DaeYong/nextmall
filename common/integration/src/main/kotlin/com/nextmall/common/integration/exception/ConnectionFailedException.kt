package com.nextmall.common.integration.exception

class ConnectionFailedException(
    context: IntegrationErrorContext,
    cause: Throwable? = null,
) : IntegrationException(
        errorCode = IntegrationErrorCode.CONNECTION_FAILED,
        expected = false,
        cause = cause,
        context = context,
    )
