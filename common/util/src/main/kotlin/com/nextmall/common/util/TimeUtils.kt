package com.nextmall.common.util

import java.time.Instant
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

    /** Current time as an Instant (UTC) */
    fun now(): Instant = Instant.now()

    /** Current time in UTC */
    fun nowUtc(): OffsetDateTime = OffsetDateTime.now(UTC_ZONE)

    /** Current time in the default zone (KST) */
    fun nowKst(): OffsetDateTime = OffsetDateTime.now(DEFAULT_ZONE)

    /** Converts a given Instant to OffsetDateTime (KST) */
    fun toKst(instant: Instant): OffsetDateTime = instant.atOffset(DEFAULT_ZONE)

    /** Converts a given Instant to OffsetDateTime (UTC) */
    fun toUtc(instant: Instant): OffsetDateTime = instant.atOffset(UTC_ZONE)

    /** Converts an OffsetDateTime from UTC to KST */
    fun toKst(offsetDateTime: OffsetDateTime): OffsetDateTime = offsetDateTime.withOffsetSameInstant(DEFAULT_ZONE)

    /** Converts an OffsetDateTime from KST to UTC */
    fun toUtc(offsetDateTime: OffsetDateTime): OffsetDateTime = offsetDateTime.withOffsetSameInstant(UTC_ZONE)

    fun toInstant(offsetDateTime: OffsetDateTime): Instant = offsetDateTime.toInstant()

    fun toInstantOrNull(offsetDateTime: OffsetDateTime?): Instant? = offsetDateTime?.toInstant()
}

fun Instant.toUtc(): OffsetDateTime =
    this.atOffset(TimeUtils.UTC_ZONE)

fun Instant?.toUtcOrNull(): OffsetDateTime? =
    this?.atOffset(TimeUtils.UTC_ZONE)

fun OffsetDateTime.toUtc(): OffsetDateTime =
    this.withOffsetSameInstant(ZoneOffset.UTC)

fun OffsetDateTime?.toUtcOrNull(): OffsetDateTime? =
    this?.withOffsetSameInstant(ZoneOffset.UTC)

fun Instant.toKst(): OffsetDateTime =
    this.atOffset(TimeUtils.DEFAULT_ZONE)

fun Instant?.toKstOrNull(): OffsetDateTime? =
    this?.atOffset(TimeUtils.DEFAULT_ZONE)

fun OffsetDateTime.toKst(): OffsetDateTime =
    this.withOffsetSameInstant(TimeUtils.DEFAULT_ZONE)

fun OffsetDateTime?.toKstOrNull(): OffsetDateTime? =
    this?.withOffsetSameInstant(TimeUtils.DEFAULT_ZONE)
