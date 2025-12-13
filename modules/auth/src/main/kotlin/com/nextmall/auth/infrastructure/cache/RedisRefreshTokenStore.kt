package com.nextmall.auth.infrastructure.cache

import com.nextmall.auth.port.output.token.RefreshTokenStore
import com.nextmall.common.redis.RedisOperator
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RedisRefreshTokenStore(
    private val redisOperator: RedisOperator,
) : RefreshTokenStore {
    override fun save(userId: Long, refreshToken: String, ttlSeconds: Long) {
        redisOperator.setValue(
            key = buildKey(userId),
            value = refreshToken,
            ttl = Duration.ofSeconds(ttlSeconds),
        )
    }

    override fun findByUserId(userId: Long): String? =
        redisOperator.getValue(buildKey(userId))

    override fun delete(userId: Long): Boolean =
        redisOperator.delete(buildKey(userId))

    private fun buildKey(userId: Long): String =
        PREFIX + userId

    companion object {
        private const val PREFIX = "auth:rt:"
    }
}
