package com.nextmall.bff.presentation.response.product

import com.nextmall.bff.client.product.response.SellerProductViewClientResponse
import com.nextmall.common.util.Money
import java.time.Instant
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.ser.std.ToStringSerializer

data class SellerProductViewResponse(
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

fun SellerProductViewClientResponse.toResponse() =
    SellerProductViewResponse(
        id = id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        saleStatus = saleStatus,
        displayStatus = displayStatus,
        sellerId = sellerId,
        category = category,
        isDeleted = isDeleted,
        createdAt = createdAt,
    )
