package com.nextmall.common.data.jooq.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jooq.Condition
import org.jooq.impl.DSL

class JooqConditionExtensionsTest : FunSpec({

    val EMAIL = DSL.field("email", String::class.java)
    val STATUS = DSL.field("status", String::class.java)
    val AGE = DSL.field("age", Int::class.java)
    val NAME = DSL.field("name", String::class.java)

    // ------------------------------------------------------------------------
    // eqIfNotNull
    // ------------------------------------------------------------------------
    test("eqIfNotNull - value가 null이면 null을 반환한다") {
        val result: Condition? = EMAIL.eqIfNotNull(null)
        result.shouldBeNull()
    }

    test("eqIfNotNull - value가 존재하면 eq 조건을 생성한다") {
        val result = EMAIL.eqIfNotNull("test@example.com")
        val expected = EMAIL.eq("test@example.com")

        result shouldBe expected
    }

    // ------------------------------------------------------------------------
    // inIfNotEmpty
    // ------------------------------------------------------------------------
    test("inIfNotEmpty - null이면 null을 반환한다") {
        val result: Condition? = AGE.inIfNotEmpty(null)
        result.shouldBeNull()
    }

    test("inIfNotEmpty - 빈 컬렉션이면 null을 반환한다") {
        val result: Condition? = AGE.inIfNotEmpty(emptyList())
        result.shouldBeNull()
    }

    test("inIfNotEmpty - 값이 있으면 IN 조건을 생성한다") {
        val values = listOf(1, 2, 3)

        val result = AGE.inIfNotEmpty(values)
        val expected = AGE.`in`(values)

        result shouldBe expected
    }

    // ------------------------------------------------------------------------
    // containsIfNotNull
    // ------------------------------------------------------------------------
    test("containsIfNotNull - null이면 null을 반환한다") {
        val result: Condition? = NAME.containsIfNotNull(null)
        result.shouldBeNull()
    }

    test("containsIfNotNull - 공백 문자열이면 null을 반환한다") {
        val result: Condition? = NAME.containsIfNotNull("   ")
        result.shouldBeNull()
    }

    test("containsIfNotNull - 정상 문자열이면 containsIgnoreCase 조건을 생성한다") {
        val result = NAME.containsIfNotNull("abc")
        val expected = NAME.containsIgnoreCase("abc")

        result shouldBe expected
    }

    // ------------------------------------------------------------------------
    // conditionList
    // ------------------------------------------------------------------------
    test("conditionList - 모든 조건이 null이면 trueCondition을 반환한다") {
        val result = conditionList(null, null, null)
        val expected = DSL.trueCondition()

        result shouldBe expected
    }

    test("conditionList - null이 아닌 조건만 AND로 묶는다") {
        val cond1 = EMAIL.eq("a@a.com")
        val cond2 = STATUS.eq("ACTIVE")

        val result = conditionList(cond1, null, cond2)
        val expected = cond1.and(cond2)

        result shouldBe expected
    }

    // ------------------------------------------------------------------------
    // andIfNotNull
    // ------------------------------------------------------------------------
    test("andIfNotNull - 둘 다 null이면 trueCondition을 반환한다") {
        val left: Condition? = null
        val right: Condition? = null

        val result = left.andIfNotNull(right)
        val expected = DSL.trueCondition()

        result shouldBe expected
    }

    test("andIfNotNull - 왼쪽이 null이면 오른쪽을 반환한다") {
        val left: Condition? = null
        val right: Condition = EMAIL.eq("abc")

        val result = left.andIfNotNull(right)

        result shouldBe right
    }

    test("andIfNotNull - 오른쪽이 null이면 왼쪽을 반환한다") {
        val left: Condition = STATUS.eq("ACTIVE")
        val right: Condition? = null

        val result = left.andIfNotNull(right)

        result shouldBe left
    }

    test("andIfNotNull - 둘 다 존재하면 AND로 결합한다") {
        val left = EMAIL.eq("a@a.com")
        val right = STATUS.eq("ACTIVE")

        val result = left.andIfNotNull(right)
        val expected = left.and(right)

        result shouldBe expected
    }
})
