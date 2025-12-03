package com.nextmall.auth.infrastructure.security

import com.nextmall.user.application.port.output.PasswordHasher
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordHasherImpl(
    private val passwordEncoder: PasswordEncoder,
) : PasswordHasher {
    override fun encode(raw: String): String {
        require(raw.isNotBlank()) { "Password cannot be blank." }

        return checkNotNull(passwordEncoder.encode(raw)) {
            "PasswordEncoder unexpectedly returned null."
        }
    }
}
