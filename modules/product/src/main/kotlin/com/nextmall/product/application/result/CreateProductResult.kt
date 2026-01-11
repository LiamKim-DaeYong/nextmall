package com.nextmall.product.application.result

import com.nextmall.product.domain.Product

data class CreateProductResult(
    val productId: Long
) {
    constructor(product: Product) : this(product.id)
}
