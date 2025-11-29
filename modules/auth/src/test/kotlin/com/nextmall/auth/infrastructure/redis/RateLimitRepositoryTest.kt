package com.nextmall.auth.infrastructure.redis

import com.nextmall.common.redis.RedisOperator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Duration

class RateLimitRepositoryTest :
    FunSpec({

        val redisOperator = mockk<RedisOperator>()
        val repository = RateLimitRepository(redisOperator)

        test("increaseFailCount는 RedisOperator.increment 결과값을 반환한다") {
            every { redisOperator.increment(any(), any()) } returns 3L

            val count = repository.increaseFailCount("test@a.com")

            count shouldBe 3L
            verify(exactly = 1) {
                redisOperator.increment(
                    key = "auth:login:attempts:test@a.com",
                    ttl = Duration.ofMinutes(5),
                )
            }
        }

        test("getFailCount는 RedisOperator.getValue 값을 Long으로 변환하여 반환한다") {
            every { redisOperator.getValue(any()) } returns "10"

            val count = repository.getFailCount("test@a.com")

            count shouldBe 10L
            verify(exactly = 1) {
                redisOperator.getValue("auth:login:attempts:test@a.com")
            }
        }

        test("getFailCount는 null이면 0을 반환한다") {
            every { redisOperator.getValue(any()) } returns null

            val count = repository.getFailCount("no@value.com")

            count shouldBe 0L
            verify(exactly = 1) {
                redisOperator.getValue("auth:login:attempts:no@value.com")
            }
        }

        test("resetFailCount는 RedisOperator.delete를 호출한다") {
            every { redisOperator.delete(any()) } returns true

            repository.resetFailCount("test@a.com")

            verify(exactly = 1) {
                redisOperator.delete("auth:login:attempts:test@a.com")
            }
        }
    })
