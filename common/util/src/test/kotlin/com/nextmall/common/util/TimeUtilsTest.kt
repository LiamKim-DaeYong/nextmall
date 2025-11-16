package com.nextmall.common.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TimeUtilsTest :
    FunSpec({

        test("DEFAULT_ZONE should be UTC+9") {
            TimeUtils.DEFAULT_ZONE shouldBe ZoneOffset.ofHours(9)
        }

        test("UTC_ZONE should be UTC") {
            TimeUtils.UTC_ZONE shouldBe ZoneOffset.UTC
        }

        test("now() should return time with DEFAULT_ZONE (UTC+9)") {
            val now = TimeUtils.now()
            now.offset shouldBe ZoneOffset.ofHours(9)
        }

        test("nowUtc() should return time with UTC offset") {
            val nowUtc = TimeUtils.nowUtc()
            nowUtc.offset shouldBe ZoneOffset.UTC
        }

        test("toKst(LocalDateTime) should convert correctly") {
            val local = LocalDateTime.of(2024, 1, 1, 10, 0)
            val result = TimeUtils.toKst(local)

            result.offset shouldBe ZoneOffset.ofHours(9)
            result.toLocalDateTime() shouldBe local
        }

        test("toUtc(LocalDateTime) should convert correctly") {
            val local = LocalDateTime.of(2024, 1, 1, 10, 0)
            val result = TimeUtils.toUtc(local)

            result.offset shouldBe ZoneOffset.UTC
            result.toLocalDateTime() shouldBe local
        }

        test("toKst(OffsetDateTime) should convert UTC -> KST correctly") {
            val utcDateTime = OffsetDateTime.of(2024, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
            val result = TimeUtils.toKst(utcDateTime)

            result.offset shouldBe ZoneOffset.ofHours(9)
            result.toLocalDateTime() shouldBe utcDateTime.toInstant().atOffset(ZoneOffset.ofHours(9)).toLocalDateTime()
        }

        test("toUtc(OffsetDateTime) should convert KST -> UTC correctly") {
            val kstDateTime = OffsetDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneOffset.ofHours(9))
            val result = TimeUtils.toUtc(kstDateTime)

            result.offset shouldBe ZoneOffset.UTC
            result.toLocalDateTime() shouldBe kstDateTime.toInstant().atOffset(ZoneOffset.UTC).toLocalDateTime()
        }
    })
