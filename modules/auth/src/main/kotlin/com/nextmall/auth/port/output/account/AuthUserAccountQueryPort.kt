package com.nextmall.auth.port.output.account

import com.nextmall.auth.application.query.account.AuthUserAccountContext
import com.nextmall.auth.domain.model.AuthProvider

interface AuthUserAccountQueryPort {
    fun findByProviderAndAccountId(
        provider: AuthProvider,
        providerAccountId: String,
    ): AuthUserAccountContext?
}
