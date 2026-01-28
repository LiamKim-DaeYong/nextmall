package com.nextmall.common.redis.stock

interface StockCacheRepository {
    fun get(productId: Long): Int?

    fun decrease(productId: Long, amount: Int): StockDecreaseResult

    fun increase(productId: Long, amount: Int): Int

    fun set(productId: Long, stock: Int)
}
