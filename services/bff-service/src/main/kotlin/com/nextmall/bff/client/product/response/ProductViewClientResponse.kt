package com.nextmall.bff.client.product.response

import com.nextmall.common.util.Money
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class ProductViewClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val price: Money,
    val stock: Int,
    @JsonSerialize(using = ToStringSerializer::class)
    val sellerId: Long,
    val category: String?,
)
