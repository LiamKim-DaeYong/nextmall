package com.nextmall.common.redis.stock

import com.nextmall.common.redis.RedisStore
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.script.DefaultRedisScript

@RedisStore
class RedisStockCacheRepository(
    private val template: StringRedisTemplate,
) : StockCacheRepository {
    private val decreaseScript =
        DefaultRedisScript<Long>().apply {
            setScriptText(DECREASE_LUA_SCRIPT)
            resultType = Long::class.java
        }
    private val decreaseOrInitScript =
        DefaultRedisScript<Long>().apply {
            setScriptText(DECREASE_OR_INIT_LUA_SCRIPT)
            resultType = Long::class.java
        }

    override fun get(productId: Long): Int? =
        template.opsForValue().get(stockKey(productId))?.toInt()

    override fun decrease(productId: Long, amount: Int): StockDecreaseResult {
        require(amount > 0) { "Amount must be positive" }

        val result =
            template.execute(
                decreaseScript,
                listOf(stockKey(productId)),
                amount.toString(),
            )

        return when (result?.toInt()) {
            null -> StockDecreaseResult.NotFound
            NOT_FOUND_CODE -> StockDecreaseResult.NotFound
            INSUFFICIENT_CODE -> StockDecreaseResult.InsufficientStock
            else -> StockDecreaseResult.Success(result.toInt())
        }
    }

    override fun decreaseOrInit(
        productId: Long,
        amount: Int,
        initialStock: Int,
    ): StockDecreaseResult {
        require(amount > 0) { "Amount must be positive" }
        require(initialStock >= 0) { "Initial stock must be non-negative" }

        val result =
            template.execute(
                decreaseOrInitScript,
                listOf(stockKey(productId)),
                amount.toString(),
                initialStock.toString(),
            )

        return when (result?.toInt()) {
            null -> StockDecreaseResult.NotFound
            INSUFFICIENT_CODE -> StockDecreaseResult.InsufficientStock
            else -> StockDecreaseResult.Success(result.toInt())
        }
    }

    override fun increase(productId: Long, amount: Int): Int {
        require(amount > 0) { "Amount must be positive" }
        val result = template.opsForValue().increment(stockKey(productId), amount.toLong()) ?: 0L
        return result.toInt()
    }

    override fun set(productId: Long, stock: Int) {
        require(stock >= 0) { "Stock must be non-negative" }
        template.opsForValue().set(stockKey(productId), stock.toString())
    }

    private fun stockKey(productId: Long): String = "stock:$productId"

    companion object {
        private const val NOT_FOUND_CODE = -1
        private const val INSUFFICIENT_CODE = -2

        private const val DECREASE_LUA_SCRIPT =
            """
            local current = redis.call('GET', KEYS[1])
            if current == false then
                return -1
            end

            local stock = tonumber(current)
            local amount = tonumber(ARGV[1])

            if stock < amount then
                return -2
            end

            redis.call('DECRBY', KEYS[1], amount)
            return stock - amount
            """

        private const val DECREASE_OR_INIT_LUA_SCRIPT =
            """
            local current = redis.call('GET', KEYS[1])
            local amount = tonumber(ARGV[1])
            local initial = tonumber(ARGV[2])

            if current == false then
                redis.call('SET', KEYS[1], initial)
                current = initial
            else
                current = tonumber(current)
            end

            if current < amount then
                return -2
            end

            redis.call('DECRBY', KEYS[1], amount)
            return current - amount
            """
    }
}
