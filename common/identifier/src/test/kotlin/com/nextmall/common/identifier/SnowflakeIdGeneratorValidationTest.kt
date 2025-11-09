package com.nextmall.common.identifier

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe

class SnowflakeIdGeneratorValidationTest :
    FunSpec({

        test("nodeId가 음수면 예외가 발생한다") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                    SnowflakeIdGenerator(nodeId = -1)
                }
            exception.message shouldBe "nodeId must be between 0 and 1023, but was -1"
        }

        test("nodeId가 1023을 초과하면 예외가 발생한다") {
            val exception =
                shouldThrow<IllegalArgumentException> {
                    SnowflakeIdGenerator(nodeId = 2000)
                }
            exception.message shouldBe "nodeId must be between 0 and 1023, but was 2000"
        }

        test("유효한 nodeId는 정상 생성된다") {
            val generator = SnowflakeIdGenerator(nodeId = 10)
            val id = generator.generate()
            id shouldBeGreaterThan 0L
        }
    })
