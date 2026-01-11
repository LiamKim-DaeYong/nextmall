package com.nextmall.common.util

import java.math.BigDecimal
import java.math.RoundingMode

@JvmInline
value class Money(
    val amount: BigDecimal,
) {
    init {
        require(amount.scale() == 2) { "Money scale must be exactly 2" }
    }

    operator fun plus(other: Money): Money = Money(amount.add(other.amount).setScale(2, RoundingMode.HALF_UP))

    operator fun minus(other: Money): Money = Money(amount.subtract(other.amount).setScale(2, RoundingMode.HALF_UP))

    operator fun times(multiplier: Int): Money =
        Money(
            amount.multiply(BigDecimal(multiplier)).setScale(2, RoundingMode.HALF_UP),
        )

    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    companion object {
        val ZERO = Money(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))

        fun of(value: String): Money = Money(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))

        fun of(value: Long): Money = Money(BigDecimal(value).setScale(2, RoundingMode.HALF_UP))

        fun of(value: BigDecimal): Money = Money(value.setScale(2, RoundingMode.HALF_UP))
    }
}
