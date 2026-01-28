package com.nextmall.bff.client.product.request

import java.math.BigDecimal

data class UpdateProductClientRequest(
    val name: String,
    val description: String?,
    val price: BigDecimal,
    val stock: Int,
    val category: String?,
)
