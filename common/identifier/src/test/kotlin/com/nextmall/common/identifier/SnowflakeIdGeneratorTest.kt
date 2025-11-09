package com.nextmall.common.identifier

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan

class SnowflakeIdGeneratorTest :
    FunSpec({

        val generator = SnowflakeIdGenerator(nodeId = 1L)

        test("ID는 증가해야 한다") {
            val id1 = generator.generate()
            val id2 = generator.generate()
            id2 shouldBeGreaterThan id1
        }

        test("Sequence rollover 시 waitNextMillis 호출되어도 정상 동작한다") {
            repeat((1L shl 12).toInt()) { generator.generate() }
            val id = generator.generate()
            id shouldBeGreaterThan 0L
        }
    })
