package com.nextmall.bff.client.product.response

import com.nextmall.common.util.Money
import java.time.Instant
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class SellerProductViewClientResponse(
    @JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String,
    val description: String?,
    val price: Money,
    val stock: Int,
    val saleStatus: String,
    val displayStatus: String,
    @JsonSerialize(using = ToStringSerializer::class)
    val sellerId: Long,
    val category: String?,
    val isDeleted: Boolean,
    val createdAt: Instant,
)
