package com.nextmall.common.data.jooq.util

import com.nextmall.common.util.TimeUtils
import org.jooq.Field
import org.jooq.Record
import org.jooq.Result
import java.time.OffsetDateTime

// ---------------------------------------------------------------------------
//  Nullable / Required Accessors
// ---------------------------------------------------------------------------

/**
 * Safely reads a nullable column from a jOOQ record.
 */
fun <T> Record.getNullable(field: Field<T?>): T? =
    this.get(field)

/**
 * Reads a NOT NULL column safely.
 *
 * Throws IllegalStateException if the value is unexpectedly null.
 */
fun <T> Record.getRequired(field: Field<T?>): T =
    this.get(field)
        ?: throw IllegalStateException("Required field '${field.name}' is null")

// ---------------------------------------------------------------------------
//  OffsetDateTime Helpers
// ---------------------------------------------------------------------------

/**
 * Converts an OffsetDateTime column value into UTC.
 */
fun Record.getUtcFromOffset(field: Field<OffsetDateTime?>): OffsetDateTime =
    getRequired(field).withOffsetSameInstant(TimeUtils.UTC_ZONE)

// ---------------------------------------------------------------------------
//  DTO Mapping
// ---------------------------------------------------------------------------

/**
 * Maps a nullable Record to a DTO. Returns null if the record is null.
 */
inline fun <reified T : Any> Record?.intoOrNull(): T? =
    this?.into(T::class.java)

// ---------------------------------------------------------------------------
//  Safe Primitive Accessors
// ---------------------------------------------------------------------------

/**
 * Returns a non-null String. null → "".
 */
fun Record.getStringOrEmpty(field: Field<String?>): String =
    this.get(field) ?: ""

/**
 * Returns a non-null Int. null → 0.
 */
fun Record.getIntOrZero(field: Field<Int?>): Int =
    this.get(field) ?: 0

/**
 * Returns a non-null Boolean. null → false.
 */
fun Record.getBooleanOrFalse(field: Field<Boolean?>): Boolean =
    this.get(field) ?: false

// ---------------------------------------------------------------------------
//  Result Mapping Helpers
// ---------------------------------------------------------------------------

/**
 * Applies a mapper function to all jOOQ result records.
 */
inline fun <R : Record, T> Result<R>.mapAll(crossinline mapper: (R) -> T): List<T> =
    this.map { mapper(it) }

/**
 * Field-based mapping shortcut:
 *   result.mapAll(NAME)
 *
 * Null values are skipped for safety.
 */
fun <R : Record, T> Result<R>.mapAll(field: Field<T?>): List<T> =
    this.mapNotNull { it.get(field) }
