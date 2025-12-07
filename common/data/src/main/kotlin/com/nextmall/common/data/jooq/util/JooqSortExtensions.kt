package com.nextmall.common.data.jooq.util

import org.jooq.Field
import org.jooq.Record
import org.jooq.SortField
import org.jooq.SelectConditionStep
import org.jooq.SelectLimitStep
import org.jooq.impl.DSL

data class SortProperty(
    val field: String,
    val direction: Direction = Direction.ASC
)

enum class Direction { ASC, DESC }

data class SortRequest(
    val orders: List<SortProperty> = emptyList()
)

/**
 * Apply multi-column sorting to a jOOQ query.
 *
 * Usage:
 *
 *  dsl.selectFrom(USERS)
 *     .where(...)
 *     .applySort(sortRequest)
 *     .paginate(pageRequest)
 */
fun <R : Record> SelectConditionStep<R>.applySort(
    sort: SortRequest,
): SelectLimitStep<R> {
    if (sort.orders.isEmpty()) {
        return this
    }

    val sortFields: List<SortField<*>> =
        sort.orders.map { order ->
            val field: Field<Any?> = DSL.field(order.field)

            when (order.direction) {
                Direction.ASC -> field.asc()
                Direction.DESC -> field.desc()
            }
        }

    return this.orderBy(sortFields)
}
