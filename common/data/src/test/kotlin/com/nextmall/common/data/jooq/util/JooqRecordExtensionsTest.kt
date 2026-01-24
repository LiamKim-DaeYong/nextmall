package com.nextmall.common.data.jooq.util

import com.nextmall.common.util.TimeUtils
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.jooq.Field
import org.jooq.Record
import org.jooq.Record5
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.time.OffsetDateTime

data class UserDto(
    val id: Long?,
    val name: String?
)

class JooqRecordExtensionsTest : FunSpec({

    // Field Definitions
    val ID: Field<Long?> = DSL.field("id", Long::class.java)
    val NAME: Field<String?> = DSL.field("name", String::class.java)
    val CREATED_AT_OFFSET: Field<OffsetDateTime?> =
        DSL.field("created_at_offset", OffsetDateTime::class.java)
    val ACTIVE: Field<Boolean?> = DSL.field("active", Boolean::class.java)
    val AGE: Field<Int?> = DSL.field("age", Int::class.java)

    val ctx = DSL.using(SQLDialect.POSTGRES)

    fun newRecord(
        id: Long? = null,
        name: String? = null,
        active: Boolean? = null,
        age: Int? = null,
        createdAtOffset: OffsetDateTime? = null,
    ): Record5<Long?, String?, Boolean?, Int?, OffsetDateTime?> {
        val record = ctx.newRecord(ID, NAME, ACTIVE, AGE, CREATED_AT_OFFSET)

        record[ID] = id
        record[NAME] = name
        record[ACTIVE] = active
        record[AGE] = age
        record[CREATED_AT_OFFSET] = createdAtOffset

        return record
    }

    // ----------------------------------------------------------------------
    // getNullable
    // ----------------------------------------------------------------------
    test("getNullable - null이면 null 반환") {
        val record = newRecord()
        record.getNullable(ID).shouldBeNull()
        record.getNullable(NAME).shouldBeNull()
    }

    test("getNullable - 정상 값 반환") {
        val record = newRecord(id = 10L)
        record.getNullable(ID) shouldBe 10L
    }

    // ----------------------------------------------------------------------
    // getRequired
    // ----------------------------------------------------------------------
    test("getRequired - null이면 예외 발생") {
        val record = newRecord()
        val error = kotlin.runCatching { record.getRequired(ID) }.exceptionOrNull()
        error!!.message shouldBe "Required field 'id' is null"
    }

    test("getRequired - 정상 값 반환") {
        val record = newRecord(id = 5L)
        record.getRequired(ID) shouldBe 5L
    }

    // ----------------------------------------------------------------------
    // OffsetDateTime → UTC 변환
    // ----------------------------------------------------------------------
    test("getUtcFromOffset - offset 기반 값을 UTC로 변환") {
        val offsetDate = OffsetDateTime.of(
            2025, 1, 1,
            12, 0, 0, 0,
            TimeUtils.DEFAULT_ZONE
        )
        val record = newRecord(createdAtOffset = offsetDate)

        val result = record.getUtcFromOffset(CREATED_AT_OFFSET)

        result.offset shouldBe TimeUtils.UTC_ZONE

        result.toInstant() shouldBe offsetDate.toInstant()
    }

    // ----------------------------------------------------------------------
    // intoOrNull
    // ----------------------------------------------------------------------
    test("intoOrNull - record가 null이면 null 반환") {
        val result = (null as Record?).intoOrNull<UserDto>()
        result.shouldBeNull()
    }

    test("intoOrNull - DTO 정상 매핑") {
        val record = newRecord(id = 1L, name = "Alice")
        val dto = record.intoOrNull<UserDto>()

        dto?.id shouldBe 1L
        dto?.name shouldBe "Alice"
    }

    // ----------------------------------------------------------------------
    // mapAll(field)
    // ----------------------------------------------------------------------
    test("mapAll(field) - 모든 레코드에서 field 값 추출") {
        val r1 = newRecord(id = 1L, name = "A")
        val r2 = newRecord(id = 2L, name = "B")

        val result = ctx.newResult(
            ID, NAME, ACTIVE, AGE, CREATED_AT_OFFSET
        ).apply {
            add(r1)
            add(r2)
        }.mapAll(NAME)

        result shouldBe listOf("A", "B")
    }

    // ----------------------------------------------------------------------
    // mapAll(mapper)
    // ----------------------------------------------------------------------
    test("mapAll(mapper) - mapper 기반 변환 수행") {
        val r1 = newRecord(id = 1L, name = "A")
        val r2 = newRecord(id = 2L, name = "B")

        val result = ctx.newResult(
            ID, NAME, ACTIVE, AGE, CREATED_AT_OFFSET
        ).apply {
            add(r1)
            add(r2)
        }.mapAll { it.get(NAME)!! }

        result shouldBe listOf("A", "B")
    }

    // ----------------------------------------------------------------------
    // Safe Getters
    // ----------------------------------------------------------------------
    test("getStringOrEmpty - null이면 빈 문자열 반환") {
        val record = newRecord()
        record.getStringOrEmpty(NAME) shouldBe ""
    }

    test("getStringOrEmpty - 값 반환") {
        val record = newRecord(name = "Bob")
        record.getStringOrEmpty(NAME) shouldBe "Bob"
    }

    test("getIntOrZero - null이면 0 반환") {
        val record = newRecord()
        record.getIntOrZero(AGE) shouldBe 0
    }

    test("getIntOrZero - 값 반환") {
        val record = newRecord(age = 30)
        record.getIntOrZero(AGE) shouldBe 30
    }

    test("getBooleanOrFalse - null이면 false 반환") {
        val record = newRecord()
        record.getBooleanOrFalse(ACTIVE) shouldBe false
    }

    test("getBooleanOrFalse - 값 반환") {
        val record = newRecord(active = true)
        record.getBooleanOrFalse(ACTIVE) shouldBe true
    }
})
