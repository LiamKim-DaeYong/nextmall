package com.nextmall.common.data.jooq.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainIgnoringCase
import org.jooq.SQLDialect
import org.jooq.impl.DSL

class JooqSortExtensionsTest : FunSpec({

    val dsl = DSL.using(SQLDialect.POSTGRES)

    test("정렬 조건이 없으면 orderBy가 적용되지 않는다") {
        val sql = dsl.select()
            .from("users")
            .where(DSL.trueCondition())
            .applySort(SortRequest(emptyList()))
            .sql

        // order by 없어야 함
        sql.lowercase() shouldContainIgnoringCase "from users"
        sql.lowercase().contains("order by") shouldBe false
    }

    test("단일 정렬 ASC 정상 적용") {
        val sort = SortRequest(
            orders = listOf(
                SortProperty("name", Direction.ASC)
            )
        )

        val sql = dsl.select()
            .from("users")
            .where(DSL.trueCondition())
            .applySort(sort)
            .sql

        sql.lowercase() shouldContainIgnoringCase "order by"
        sql.lowercase() shouldContainIgnoringCase "name asc"
    }

    test("단일 정렬 DESC 정상 적용") {
        val sort = SortRequest(
            orders = listOf(SortProperty("age", Direction.DESC))
        )

        val sql = dsl.select()
            .from("users")
            .where(DSL.trueCondition())
            .applySort(sort)
            .sql

        sql.lowercase() shouldContainIgnoringCase "age desc"
    }

    test("다중 정렬 정상 적용") {
        val sort = SortRequest(
            orders = listOf(
                SortProperty("age", Direction.DESC),
                SortProperty("name", Direction.ASC),
            )
        )

        val sql = dsl.select()
            .from("users")
            .where(DSL.trueCondition())
            .applySort(sort)
            .sql

        sql.lowercase() shouldContainIgnoringCase "age desc"
        sql.lowercase() shouldContainIgnoringCase "name asc"

        val orderIndex1 = sql.lowercase().indexOf("age desc")
        val orderIndex2 = sql.lowercase().indexOf("name asc")
        (orderIndex1 < orderIndex2) shouldBe true
    }
})
