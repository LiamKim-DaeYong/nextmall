package com.nextmall.apigateway.security.exception

import org.springframework.security.core.AuthenticationException

class InvalidJwtAuthenticationException(
    message: String,
) : AuthenticationException(message)
