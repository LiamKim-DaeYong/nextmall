package com.nextmall.common.identifier

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeGreaterThan

class SnowflakeIdGeneratorEdgeCaseTest :
    FunSpec({

        test("waitNextMillis는 lastTimestamp보다 큰 값을 반환한다") {
            val generator = SnowflakeIdGenerator(1L)
            val method = generator::class.java.getDeclaredMethod("waitNextMillis", Long::class.java)
            method.isAccessible = true

            val lastTs = System.currentTimeMillis()
            val result = method.invoke(generator, lastTs) as Long

            result shouldBeGreaterThan lastTs
        }
    })
