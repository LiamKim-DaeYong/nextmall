package com.nextmall.common.data.jpa.converter

import com.nextmall.common.util.Money
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.math.BigDecimal

@Converter(autoApply = false)
class MoneyConverter : AttributeConverter<Money, BigDecimal> {
    override fun convertToDatabaseColumn(money: Money?): BigDecimal? =
        money?.amount

    override fun convertToEntityAttribute(value: BigDecimal?): Money? =
        value?.let { Money.of(it) }
}
