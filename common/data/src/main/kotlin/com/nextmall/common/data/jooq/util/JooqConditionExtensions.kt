package com.nextmall.common.data.jooq.util

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

/**
 * Applies an equality condition only if the given value is not null.
 *
 * Example:
 *   USERS.EMAIL.eqIfNotNull(email)
 *
 * Returns:
 *   - Condition -> if value != null
 *   - null -> if value == null
 */
fun <T> Field<T>.eqIfNotNull(value: T?): Condition? =
    value?.let { this.eq(it) }

/**
 * Applies an IN condition only if the collection is not empty.
 *
 * Example:
 *   USERS.ID.inIfNotEmpty(idList)
 *
 * Returns:
 *   - Condition -> if values is not null and not empty
 *   - null -> otherwise
 */
fun <T> Field<T>.inIfNotEmpty(values: Collection<T>?): Condition? =
    if (!values.isNullOrEmpty()) this.`in`(values) else null

/**
 * Applies a case-insensitive LIKE %value% condition only if the
 * given value is not blank.
 *
 * Example:
 *   USERS.NICKNAME.containsIfNotNull(keyword)
 */
fun Field<String>.containsIfNotNull(value: String?): Condition? =
    value?.takeIf { it.isNotBlank() }
        ?.let { this.containsIgnoreCase(it) }

/**
 * Combine nullable conditions safely.
 *
 * Example:
 *   where(
 *     conditionList(
 *       USERS.EMAIL.eqIfNotNull(email),
 *       USERS.STATUS.eqIfNotNull(status),
 *       USERS.ROLE.eqIfNotNull(role)
 *     )
 *   )
 *
 * Behavior:
 *   - If all conditions are null → returns TRUE condition.
 *   - Otherwise → AND-chain all non-null conditions.
 */
fun conditionList(vararg conditions: Condition?): Condition {
    val list = conditions.filterNotNull()
    return if (list.isEmpty()) DSL.trueCondition()
    else list.reduce { acc, cond -> acc.and(cond) }
}

/**
 * Combine two nullable conditions safely.
 *
 * Example:
 *   val cond = c1.andIfNotNull(c2).andIfNotNull(c3)
 *
 * Behavior:
 *   - If both are null → TRUE
 *   - If one is null → return the other
 *   - If both exist → AND them
 *
 * Useful for incremental condition building.
 */
fun Condition?.andIfNotNull(other: Condition?): Condition =
    when {
        this == null && other == null -> DSL.trueCondition()
        this == null -> other!!
        other == null -> this
        else -> this.and(other)
    }
