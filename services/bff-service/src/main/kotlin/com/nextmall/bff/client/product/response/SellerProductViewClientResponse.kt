package com.nextmall.bff.client.product.response

import com.nextmall.common.util.Money
import java.time.Instant

data class SellerProductViewClientResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Money,
    val stock: Int,
    val saleStatus: String,
    val displayStatus: String,
    val sellerId: Long,
    val category: String?,
    val isDeleted: Boolean,
    val createdAt: Instant,
)
