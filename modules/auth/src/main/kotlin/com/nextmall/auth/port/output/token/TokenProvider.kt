package com.nextmall.auth.port.output.token

import com.nextmall.auth.domain.model.TokenClaims

interface TokenProvider {
    /** ACCESS TOKEN 생성 */
    fun generateAccessToken(userId: Long, roles: List<String> = emptyList()): String

    /** REFRESH TOKEN 생성 */
    fun generateRefreshToken(userId: Long): String

    /** REFRESH TOKEN 검증 및 userId 추출 */
    fun parseRefreshToken(token: String): Long

    /** ACCESS TOKEN 검증 및 Claims 반환 (subject = userId, roles 포함) */
    fun parseAccessToken(token: String): TokenClaims?

    /** refresh token TTL */
    fun refreshTokenTtlSeconds(): Long
}
