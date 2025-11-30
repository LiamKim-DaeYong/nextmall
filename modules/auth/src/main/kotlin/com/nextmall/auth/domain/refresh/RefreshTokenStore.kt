package com.nextmall.auth.domain.refresh

interface RefreshTokenStore {
    fun save(userId: Long, refreshToken: String, ttlSeconds: Long)

    fun findByUserId(userId: Long): String?

    fun delete(userId: Long): Boolean
}
