package com.nextmall.common.redis

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisOperator(
    private val template: StringRedisTemplate,
) {
    fun increment(key: String, ttl: Duration? = null): Long {
        val count = template.opsForValue().increment(key) ?: 0L

        if (ttl != null && template.getExpire(key) == -1L) {
            template.expire(key, ttl)
        }

        return count
    }

    fun getValue(key: String): String? =
        template.opsForValue().get(key) // NOSONAR

    fun delete(key: String) {
        template.delete(key)
    }
}
