package com.nextmall.auth.domain.model

import com.nextmall.user.domain.model.AuthProvider

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
