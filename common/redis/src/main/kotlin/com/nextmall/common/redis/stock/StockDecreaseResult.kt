package com.nextmall.common.redis.stock

sealed class StockDecreaseResult {
    data class Success(
        val remaining: Int,
    ) : StockDecreaseResult()

    data object InsufficientStock : StockDecreaseResult()

    data object NotFound : StockDecreaseResult()
}
