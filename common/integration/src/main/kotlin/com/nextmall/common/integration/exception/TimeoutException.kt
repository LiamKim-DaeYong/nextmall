package com.nextmall.common.integration.exception

class TimeoutException(
    context: IntegrationErrorContext,
    cause: Throwable? = null,
) : IntegrationException(
        errorCode = IntegrationErrorCode.TIMEOUT,
        expected = false,
        cause = cause,
        context = context,
    )
