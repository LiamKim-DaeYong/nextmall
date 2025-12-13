package com.nextmall.auth.domain.model

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
