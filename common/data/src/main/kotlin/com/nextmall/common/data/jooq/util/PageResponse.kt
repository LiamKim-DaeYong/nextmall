package com.nextmall.common.data.jooq.util

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

fun <T, R> PageResult<T>.toResponse(transform: (T) -> R): PageResponse<R> =
    PageResponse(
        content = content.map(transform),
        page = page,
        size = size,
        totalElements = total,
        totalPages = ((total + size - 1) / size).toInt(),
    )
