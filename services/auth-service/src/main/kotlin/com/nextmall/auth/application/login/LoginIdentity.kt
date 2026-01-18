package com.nextmall.auth.application.login

import com.nextmall.auth.domain.account.AuthProvider

data class LoginIdentity(
    val provider: AuthProvider,
    val identifier: String,
) {
    companion object {
        fun local(email: String) =
            LoginIdentity(
                provider = AuthProvider.LOCAL,
                identifier = email,
            )
    }
}
