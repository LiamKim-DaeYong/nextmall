package com.nextmall.common.redis

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.time.Duration

class RedisOperatorTest :
    FunSpec({

        val template = mockk<StringRedisTemplate>()
        val valueOps = mockk<ValueOperations<String, String>>()

        lateinit var operator: RedisOperator

        beforeTest {
            clearMocks(template, valueOps)
            operator = RedisOperator(template)
            every { template.opsForValue() } returns valueOps
        }

        test("increment는 opsForValue.increment 호출 후 TTL이 없으면 expire를 설정한다") {
            // given
            every { valueOps.increment("test-key1") } returns 5L
            every { template.getExpire("test-key1") } returns -1L
            every { template.expire("test-key1", Duration.ofMinutes(3)) } returns true

            // when
            val result = operator.increment("test-key1", Duration.ofMinutes(3))

            // then
            result shouldBe 5L

            verifyOrder {
                valueOps.increment("test-key1")
                template.getExpire("test-key1")
                template.expire("test-key1", Duration.ofMinutes(3))
            }
        }

        test("getValue는 Redis의 값을 그대로 반환한다") {
            // given
            every { valueOps.get("hello") } returns "world"

            // when
            val result = operator.getValue("hello")

            // then
            result shouldBe "world"

            verify(exactly = 1) {
                valueOps.get("hello")
            }
        }

        test("delete는 template.delete 호출한다") {
            // given
            every { template.delete("aaa") } returns true

            // when
            val result = operator.delete("aaa")

            // then
            result shouldBe true

            verify(exactly = 1) {
                template.delete("aaa")
            }
        }

        test("increment는 TTL이 있어도 기존 TTL이 설정되어 있으면 expire를 호출하지 않는다") {
            // given
            every { valueOps.increment("test-key2") } returns 10L

            // TTL이 이미 설정된 경우 (예: 120초 남음)
            every { template.getExpire("test-key2") } returns 120L

            // expire는 호출되지 않아야 한다
            every { template.expire("test-key2", any()) } returns true

            // when
            val result = operator.increment("test-key2", Duration.ofMinutes(3))

            // then
            result shouldBe 10L

            verify(exactly = 1) { valueOps.increment("test-key2") }
            verify(exactly = 1) { template.getExpire("test-key2") }
            verify(exactly = 0) { template.expire("test-key2", any()) }
        }

        test("setValue는 값을 저장하고 TTL이 있으면 expire를 설정한다") {
            // given
            every { valueOps.set("key1", "value1") } returns Unit
            every { template.expire("key1", Duration.ofSeconds(30)) } returns true

            // when
            operator.setValue("key1", "value1", Duration.ofSeconds(30))

            // then
            verifyOrder {
                valueOps.set("key1", "value1")
                template.expire("key1", Duration.ofSeconds(30))
            }
        }

        test("setValue는 TTL이 null이면 expire를 호출하지 않는다") {
            // given
            every { valueOps.set("key2", "value2") } returns Unit
            every { template.expire(any(), any()) } returns true

            // when
            operator.setValue("key2", "value2", null)

            // then
            verify(exactly = 1) { valueOps.set("key2", "value2") }
            verify(exactly = 0) { template.expire("key2", any()) }
        }

        test("exists는 해당 키가 존재하면 true를 반환한다") {
            // given
            every { template.hasKey("exists-key") } returns true

            // when
            val result = operator.exists("exists-key")

            // then
            result shouldBe true
            verify(exactly = 1) { template.hasKey("exists-key") }
        }

        test("exists는 키가 없으면 false를 반환한다") {
            // given
            every { template.hasKey("none-key") } returns false

            // when
            val result = operator.exists("none-key")

            // then
            result shouldBe false
            verify(exactly = 1) { template.hasKey("none-key") }
        }
    })
