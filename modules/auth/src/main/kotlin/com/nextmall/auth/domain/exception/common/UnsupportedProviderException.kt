package com.nextmall.auth.domain.exception.common

import com.nextmall.auth.domain.model.AuthProvider

class UnsupportedProviderException(
    val provider: AuthProvider,
) : RuntimeException("Authentication provider '$provider' is not supported")
