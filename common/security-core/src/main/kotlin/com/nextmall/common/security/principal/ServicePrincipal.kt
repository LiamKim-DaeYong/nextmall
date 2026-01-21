package com.nextmall.common.security.principal

data class ServicePrincipal(
    val serviceName: String,
    val scope: String,
)
