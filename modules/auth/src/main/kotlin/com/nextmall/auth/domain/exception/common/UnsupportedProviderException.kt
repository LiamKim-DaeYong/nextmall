package com.nextmall.auth.domain.exception.common

import com.nextmall.auth.domain.model.AuthProvider

class UnsupportedProviderException(
    provider: AuthProvider,
) : RuntimeException()
