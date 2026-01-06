package com.nextmall.common.redis

import org.springframework.data.redis.core.StringRedisTemplate
import java.time.Duration

@RedisStore
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

    fun setValue(key: String, value: String, ttl: Duration? = null) {
        template.opsForValue().set(key, value) // NOSONAR
        if (ttl != null) {
            template.expire(key, ttl)
        }
    }

    fun delete(key: String): Boolean =
        template.delete(key)

    fun exists(key: String): Boolean =
        template.hasKey(key)
}
