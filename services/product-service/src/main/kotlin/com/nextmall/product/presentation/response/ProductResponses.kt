package com.nextmall.product.presentation.response

import com.nextmall.common.util.Money
import com.nextmall.product.application.query.ProductView
import com.nextmall.product.domain.DisplayStatus
import com.nextmall.product.domain.SaleStatus
import java.time.Instant

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Money,
    val stock: Int,
    val saleStatus: SaleStatus,
    val displayStatus: DisplayStatus,
    val category: String?,
    val createdAt: Instant,
)

data class SellerProductResponse(
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

fun ProductView.toPublicResponse() =
    ProductResponse(
        id = id,
        name = name,
        description = description,
        price = price,
        stock = stock,
        saleStatus = saleStatus,
        displayStatus = displayStatus,
        category = category,
        createdAt = createdAt,
    )

fun ProductView.toSellerResponse() =
    SellerProductResponse(
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
