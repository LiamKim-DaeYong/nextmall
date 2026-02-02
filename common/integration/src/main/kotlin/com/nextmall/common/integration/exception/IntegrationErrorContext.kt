package com.nextmall.common.integration.exception

import com.nextmall.common.exception.ErrorResponse

data class IntegrationErrorContext(
    val url: String?,
    val statusCode: Int?,
    val responseBody: String?,
    val errorResponse: ErrorResponse? = null,
    val serviceName: String? = null,
)
