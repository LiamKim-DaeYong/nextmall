package com.nextmall.common.identifier

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class IdentifierPropertiesTest :
    FunSpec({

        test("기본 nodeId는 1이다") {
            IdentifierProperties().nodeId shouldBe 1L
        }

        test("nodeId를 설정하면 반영된다") {
            val props = IdentifierProperties(nodeId = 99L)
            props.nodeId shouldBe 99L
        }
    })
