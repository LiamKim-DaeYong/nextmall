package com.nextmall.bff.application.product.command

import com.nextmall.common.util.Money

data class CreateProductCommand(
    val name: String,
    val price: Money,
    val stock: Int,
    val category: String?,
)
