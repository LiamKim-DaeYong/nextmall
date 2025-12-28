package com.nextmall.auth.infrastructure.cache

import com.nextmall.common.redis.RedisOperator
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RefreshTokenRepository(
    private val redisOperator: RedisOperator,
) {
    fun save(
        refreshToken: String,
        authAccountId: Long,
        ttlSeconds: Long,
    ) {
        redisOperator.setValue(
            key = buildKey(refreshToken),
            value = authAccountId.toString(),
            ttl = Duration.ofSeconds(ttlSeconds),
        )
    }

    fun findAuthAccountId(
        refreshToken: String,
    ): Long? =
        redisOperator
            .getValue(buildKey(refreshToken))
            ?.toLongOrNull()

    fun delete(
        refreshToken: String,
    ): Boolean =
        redisOperator.delete(buildKey(refreshToken))

    private fun buildKey(refreshToken: String): String =
        PREFIX + refreshToken

    companion object {
        private const val PREFIX = "auth:refresh:"
    }
}
