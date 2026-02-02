package com.nextmall.common.web.core.security.principal

data class ServicePrincipal(
    val serviceName: String,
    val scope: String,
)
