package com.nextmall.common.data.jooq.util

import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.DSL

/**
 * Basic pagination using limit/offset.
 *
 * Example:
 *  dsl.selectFrom(USERS).paginate(0, 20)
 */
fun <R : Record> SelectLimitStep<R>.paginate(page: Int, size: Int) =
    this.limit(size).offset(page * size)

/**
 * Page request abstraction for jOOQ.
 * Similar to Spring's Pageable, but lightweight.
 */
data class PageRequest(
    val page: Int = 0,
    val size: Int = 20
) {
    init {
        require(page >= 0) { "page must be >= 0" }
        require(size > 0) { "size must be > 0" }
    }
}

/**
 * Convenience overload: paginate(PageRequest)
 */
fun <R : Record> SelectLimitStep<R>.paginate(page: PageRequest) =
    this.paginate(page.page, page.size)

/**
 * Pagination response wrapper.
 * Contains both data and total count.
 */
data class PageResult<T>(
    val content: List<T>,
    val total: Long,
    val page: Int,
    val size: Int
)

/**
 * Fetch paginated result with total count.
 *
 * Example:
 *  dsl.selectFrom(USERS)
 *     .fetchPage(0, 20) { record -> UserDetail(...) }
 */
fun <R : Record, T> SelectLimitStep<R>.fetchPage(
    page: Int,
    size: Int,
    mapper: (R) -> T
): PageResult<T> {
    val offset = page.toLong() * size

    // COUNT(*) query
    val total = DSL.using(this.configuration())
        .fetchCount(this.asTable())
        .toLong()

    // data query
    val items = this
        .limit(size)
        .offset(offset)
        .fetch(mapper)
        .toList()

    return PageResult(
        content = items,
        total = total,
        page = page,
        size = size
    )
}

/**
 * Overload supporting PageRequest.
 */
fun <R : Record, T> SelectLimitStep<R>.fetchPage(
    page: PageRequest,
    mapper: (R) -> T
): PageResult<T> =
    this.fetchPage(page.page, page.size, mapper)
