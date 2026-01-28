package com.nextmall.bff.client.product.request

import java.math.BigDecimal

data class CreateProductClientRequest(
    val name: String,
    val price: BigDecimal,
    val stock: Int,
    val category: String?,
)
