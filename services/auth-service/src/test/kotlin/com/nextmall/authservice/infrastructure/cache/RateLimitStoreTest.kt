package com.nextmall.authservice.infrastructure.cache

import com.nextmall.auth.application.login.LoginIdentity
import com.nextmall.auth.infrastructure.cache.RateLimitStore
import com.nextmall.common.testsupport.annotation.RedisIntegrationTest
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

@RedisIntegrationTest
class RateLimitStoreTest(
    private val rateLimitStore: RateLimitStore,
) : FunSpec({

        test("increaseFailCount는 호출할 때마다 1씩 증가한다") {
            val identity = LoginIdentity.local("test-increment@example.com")

            val first = rateLimitStore.increaseFailCount(identity)
            val second = rateLimitStore.increaseFailCount(identity)
            val third = rateLimitStore.increaseFailCount(identity)

            first shouldBe 1L
            second shouldBe 2L
            third shouldBe 3L

            // cleanup
            rateLimitStore.resetFailCount(identity)
        }

        test("getFailCount는 현재 실패 횟수를 반환한다") {
            val identity = LoginIdentity.local("test-get@example.com")

            rateLimitStore.increaseFailCount(identity)
            rateLimitStore.increaseFailCount(identity)

            val count = rateLimitStore.getFailCount(identity)

            count shouldBe 2L

            // cleanup
            rateLimitStore.resetFailCount(identity)
        }

        test("getFailCount는 키가 없으면 0을 반환한다") {
            val identity = LoginIdentity.local("non-existent@example.com")

            val count = rateLimitStore.getFailCount(identity)

            count shouldBe 0L
        }

        test("resetFailCount는 실패 횟수를 초기화한다") {
            val identity = LoginIdentity.local("test-reset@example.com")

            rateLimitStore.increaseFailCount(identity)
            rateLimitStore.increaseFailCount(identity)
            rateLimitStore.getFailCount(identity) shouldBeGreaterThan 0L

            rateLimitStore.resetFailCount(identity)

            rateLimitStore.getFailCount(identity) shouldBe 0L
        }
    })
