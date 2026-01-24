package com.nextmall.product.application.query

import com.nextmall.common.util.Money
import com.nextmall.product.domain.DisplayStatus
import com.nextmall.product.domain.SaleStatus
import java.time.Instant

data class ProductView(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Money,
    val stock: Int,
    val saleStatus: SaleStatus,
    val displayStatus: DisplayStatus,
    val sellerId: Long,
    val category: String?,
    val isDeleted: Boolean,
    val createdAt: Instant,
)
