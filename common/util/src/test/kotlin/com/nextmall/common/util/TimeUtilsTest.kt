package com.nextmall.common.util

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.longs.shouldBeLessThan
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.math.abs

class TimeUtilsTest :
    FunSpec({

        test("DEFAULT_ZONE should be UTC+9") {
            TimeUtils.DEFAULT_ZONE shouldBe ZoneOffset.ofHours(9)
        }

        test("UTC_ZONE should be UTC") {
            TimeUtils.UTC_ZONE shouldBe ZoneOffset.UTC
        }

        test("now() should return near-current Instant") {
            val now = TimeUtils.now()
            val diffMillis = abs(Instant.now().toEpochMilli() - now.toEpochMilli())
            diffMillis.shouldBeLessThan(2000)
        }

        test("nowUtc() should return time with UTC offset") {
            val nowUtc = TimeUtils.nowUtc()
            nowUtc.offset shouldBe ZoneOffset.UTC
        }

        test("nowKst() should return time with DEFAULT_ZONE (UTC+9)") {
            val nowKst = TimeUtils.nowKst()
            nowKst.offset shouldBe ZoneOffset.ofHours(9)
        }

        test("toKst(Instant) should convert correctly") {
            val instant = Instant.parse("2024-01-01T01:00:00Z")
            val result = TimeUtils.toKst(instant)

            result.offset shouldBe ZoneOffset.ofHours(9)
            result.toInstant() shouldBe instant
        }

        test("toUtc(Instant) should convert correctly") {
            val instant = Instant.parse("2024-01-01T01:00:00Z")
            val result = TimeUtils.toUtc(instant)

            result.offset shouldBe ZoneOffset.UTC
            result.toInstant() shouldBe instant
        }

        test("toKst(OffsetDateTime) should convert UTC -> KST correctly") {
            val utcDateTime = OffsetDateTime.of(2024, 1, 1, 1, 0, 0, 0, ZoneOffset.UTC)
            val result = TimeUtils.toKst(utcDateTime)

            result.offset shouldBe ZoneOffset.ofHours(9)
            result.toInstant() shouldBe utcDateTime.toInstant()
        }

        test("toUtc(OffsetDateTime) should convert KST -> UTC correctly") {
            val kstDateTime = OffsetDateTime.of(2024, 1, 1, 10, 0, 0, 0, ZoneOffset.ofHours(9))
            val result = TimeUtils.toUtc(kstDateTime)

            result.offset shouldBe ZoneOffset.UTC
            result.toInstant() shouldBe kstDateTime.toInstant()
        }
    })
