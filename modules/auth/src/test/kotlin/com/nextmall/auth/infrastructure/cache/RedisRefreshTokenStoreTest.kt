package com.nextmall.auth.infrastructure.cache

import com.nextmall.common.redis.RedisOperator
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify

class RedisRefreshTokenStoreTest :
    FunSpec({

        val redisOperator = mockk<RedisOperator>()
        lateinit var store: RedisRefreshTokenStore

        beforeTest {
            io.mockk.clearMocks(redisOperator)
            store = RedisRefreshTokenStore(redisOperator)
        }

        test("save는 RedisOperator.setValue를 호출한다") {
            // given
            every { redisOperator.setValue("auth:rt:1", "token", any()) } just Runs

            // when
            store.save(1L, "token", 3600)

            // then
            verify(exactly = 1) {
                redisOperator.setValue("auth:rt:1", "token", any())
            }
        }

        test("delete는 RedisOperator.delete를 호출한다") {
            // given
            every { redisOperator.delete("auth:rt:3") } returns true

            // when
            val result = store.delete(3L)

            // then
            result shouldBe true
            verify(exactly = 1) {
                redisOperator.delete("auth:rt:3")
            }
        }

        test("findByUserId는 RedisOperator.getValue를 호출한다") {
            // given
            every { redisOperator.getValue("auth:rt:5") } returns "refresh-token"

            // when
            val result = store.findByUserId(5L)

            // then
            result shouldBe "refresh-token"
            verify(exactly = 1) {
                redisOperator.getValue("auth:rt:5")
            }
        }
    })
