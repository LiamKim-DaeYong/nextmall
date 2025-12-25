package com.nextmall.auth.domain.token

import java.time.Instant

data class TokenClaims(
    val userId: Long,
    val roles: List<String>,
    val expirationTime: Instant,
)
