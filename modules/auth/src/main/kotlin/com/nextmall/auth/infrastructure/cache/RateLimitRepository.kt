package com.nextmall.auth.infrastructure.cache

import com.nextmall.auth.domain.model.LoginIdentity
import com.nextmall.common.redis.RedisOperator
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class RateLimitRepository(
    private val redisOperator: RedisOperator,
) {
    fun increaseFailCount(identity: LoginIdentity): Long =
        redisOperator.increment(
            key = buildKey(identity),
            ttl = BLOCK_TTL,
        )

    fun getFailCount(identity: LoginIdentity): Long =
        redisOperator.getValue(buildKey(identity))?.toLong() ?: 0L

    fun resetFailCount(identity: LoginIdentity) {
        redisOperator.delete(buildKey(identity))
    }

    private fun buildKey(identity: LoginIdentity): String =
        PREFIX + identity.provider.name.lowercase() + ":" + identity.identifier

    companion object {
        private const val PREFIX = "auth:login:attempts:"
        private val BLOCK_TTL = Duration.ofMinutes(5)
    }
}
