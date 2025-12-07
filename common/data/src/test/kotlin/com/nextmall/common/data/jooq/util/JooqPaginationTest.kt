package com.nextmall.common.data.jooq.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.h2.jdbcx.JdbcDataSource
import org.jooq.SQLDialect
import org.jooq.impl.DSL

class JooqPaginationExtensionsTest : FunSpec({

    val dataSource = JdbcDataSource().apply {
        setURL("jdbc:h2:mem:test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1")
        user = "sa"
        password = ""
    }

    val dsl = DSL.using(dataSource, SQLDialect.POSTGRES)

    @file:Suppress("SqlDialectInspection")
    beforeTest {
        dsl.execute("CREATE TABLE IF NOT EXISTS test_table (id INT)")
        dsl.execute("DELETE FROM test_table")
        dsl.execute("INSERT INTO test_table(id) VALUES (1), (2)")
    }

    test("paginate(page, size)는 LIMIT과 OFFSET 값을 내부적으로 올바르게 설정한다") {
        val select = dsl
            .select(DSL.field("id"))
            .from("test_table")
            .paginate(page = 2, size = 10)

        select.bindValues[0] shouldBe 20L  // offset
        select.bindValues[1] shouldBe 10L  // limit
    }

    test("paginate(PageRequest)는 내부적으로 paginate(page, size)를 호출한다") {
        val page = PageRequest(page = 1, size = 5)

        val select = dsl
            .select(DSL.field("id"))
            .from("test_table")
            .paginate(page)

        select.bindValues[0] shouldBe 5L  // offset (1 * 5)
        select.bindValues[1] shouldBe 5L  // limit
    }

    test("fetchPage(page, size)는 total과 content를 올바르게 계산한다") {
        val result = dsl
            .select(DSL.field("id"))
            .from("test_table")
            .fetchPage(page = 0, size = 1) { record ->
                record.get("id", Int::class.java)
            }

        result.total shouldBe 2L
        result.page shouldBe 0
        result.size shouldBe 1
        result.content shouldContain 1
    }

    test("fetchPage(PageRequest)는 fetchPage(page, size)를 내부적으로 호출한다") {
        val pageReq = PageRequest(page = 1, size = 1)

        val result = dsl
            .select(DSL.field("id"))
            .from("test_table")
            .fetchPage(pageReq) { record ->
                record.get("id", Int::class.java)
            }

        result.total shouldBe 2L
        result.page shouldBe 1
        result.size shouldBe 1
        result.content shouldContain 2
    }
})
