package com.nextmall.auth.infrastructure.redis

import com.nextmall.common.redis.RedisOperator
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RateLimitRepository(
    private val redisOperator: RedisOperator,
) {
    fun increaseFailCount(key: String): Long =
        redisOperator.increment(
            key = PREFIX + key,
            ttl = BLOCK_TTL,
        )

    fun getFailCount(key: String): Long =
        redisOperator.getValue(PREFIX + key)?.toLong() ?: 0L

    fun resetFailCount(key: String) {
        redisOperator.delete(PREFIX + key)
    }

    companion object {
        private const val PREFIX = "auth:login:attempts:"
        private val BLOCK_TTL = Duration.ofMinutes(5)
    }
}
