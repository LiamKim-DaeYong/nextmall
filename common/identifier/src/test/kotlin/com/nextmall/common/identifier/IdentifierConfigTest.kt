package com.nextmall.common.identifier

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe

class IdentifierConfigTest :
    FunSpec({

        test("IdGenerator 빈이 정상 생성된다") {
            val props = IdentifierProperties(nodeId = 1L)
            val config = IdentifierConfig(props)

            val generator = config.idGenerator()
            generator shouldNotBe null
        }
    })
