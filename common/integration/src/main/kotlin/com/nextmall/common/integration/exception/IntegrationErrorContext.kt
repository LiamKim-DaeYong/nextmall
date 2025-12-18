package com.nextmall.common.integration.exception

data class IntegrationErrorContext(
    val url: String?,
    val statusCode: Int?,
    val responseBody: String?,
)
