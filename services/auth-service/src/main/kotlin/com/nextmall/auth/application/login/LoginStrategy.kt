package com.nextmall.auth.application.login

import com.nextmall.auth.domain.account.AuthProvider

interface LoginStrategy {
    fun supports(provider: AuthProvider): Boolean

    fun authenticate(
        identifier: String,
        credential: String?,
    ): Long
}
