package com.nextmall.common.util

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Utility class for time and date operations.
 * Provides consistent zone offsets for global and local contexts.
 */
object TimeUtils {
    /** Default application timezone (Asia/Seoul, UTC+9) */
    val DEFAULT_ZONE: ZoneOffset = ZoneOffset.ofHours(9)

    /** Standard UTC timezone offset */
    val UTC_ZONE: ZoneOffset = ZoneOffset.UTC

    /** Current time in the default zone (KST) */
    fun now(): OffsetDateTime = OffsetDateTime.now(DEFAULT_ZONE)

    /** Current time in UTC */
    fun nowUtc(): OffsetDateTime = OffsetDateTime.now(UTC_ZONE)

    /** Converts a given LocalDateTime to OffsetDateTime (KST) */
    fun toKst(localDateTime: LocalDateTime): OffsetDateTime = localDateTime.atOffset(DEFAULT_ZONE)

    /** Converts a given LocalDateTime to OffsetDateTime (UTC) */
    fun toUtc(localDateTime: LocalDateTime): OffsetDateTime = localDateTime.atOffset(UTC_ZONE)

    /** Converts an OffsetDateTime from UTC to KST */
    fun toKst(offsetDateTime: OffsetDateTime): OffsetDateTime = offsetDateTime.withOffsetSameInstant(DEFAULT_ZONE)

    /** Converts an OffsetDateTime from KST to UTC */
    fun toUtc(offsetDateTime: OffsetDateTime): OffsetDateTime = offsetDateTime.withOffsetSameInstant(UTC_ZONE)

    fun toUtcOrNull(localDateTime: LocalDateTime?): OffsetDateTime? =
        localDateTime?.atOffset(UTC_ZONE)

    fun toKstOrNull(localDateTime: LocalDateTime?): OffsetDateTime? =
        localDateTime?.atOffset(DEFAULT_ZONE)
}

fun LocalDateTime.toUtc(): OffsetDateTime =
    this.atOffset(TimeUtils.UTC_ZONE)

fun LocalDateTime.toKst(): OffsetDateTime =
    this.atOffset(TimeUtils.DEFAULT_ZONE)
